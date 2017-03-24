package org.armanious.synchronization;

import java.util.HashSet;

public class DependentRunnable implements Runnable {

	private final Runnable r;
	private Thread thread;
	private final HashSet<DependentRunnable> dependencies = new HashSet<>();

	private boolean ran = false;

	public DependentRunnable(Runnable r, DependentRunnable...dependencies) {
		this.r = r;
		thread = new Thread(this, r.getClass().getSimpleName());
		for(DependentRunnable dr : dependencies){
			this.dependencies.add(dr);
		}
	}

	public void addDependency(DependentRunnable dr){
		dependencies.add(dr);
	}

	public DependentRunnable[] getDependencies(){
		return dependencies.toArray(new DependentRunnable[dependencies.size()]);
	}
	
	public void run(){
		for(DependentRunnable dependency : dependencies){
			synchronized(dependency){
				if(!dependency.isDone()){
					try {
						dependency.wait();
					} catch (InterruptedException e) {
						System.out.println("DependentRunnable successfully received interrupt.");
						return;
					}
				}
			}
		}
		if(Thread.interrupted()){
			System.out.println("DependentRunnable successfully received interrupt.2");
			return;
		}
		r.run();
		ran = true;
		if(Thread.interrupted()){
			System.out.println("DependentRunnable successfully received interrupt, although the runnable executed successfully already.");
			return;
		}
		synchronized(this){
			this.notifyAll();
		}
	}

	public boolean isDone(){
		synchronized(this){
			return ran;
		}
	}

	public void reset(){
		synchronized(this){
			ran = false;
			thread = new Thread(this, r.getClass().getSimpleName());
		}
	}
	
	public Thread getAssociatedThread(){
		return thread;
	}
	
}
