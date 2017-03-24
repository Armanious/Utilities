package org.armanious.math;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Primes {

	private static int[] PRIMES;
	static {
		final File file = new File(System.getProperty("java.io.tmpdir"), "primes.dat");
		try {
			if(!file.exists()){
				file.createNewFile();
				final int MAX = 20000000;
				final boolean[] composites = new boolean[MAX];
				int numPrimes = 0;

				//final long start = System.currentTimeMillis();
				composites[0] = composites[1] = true;
				int p = 2;
				numPrimes++;
				do{
					numPrimes++;
					for(int copy = p * 2; copy < MAX && copy > 0; copy += p){
						composites[copy] = true;
					}
				}while((p = search(composites, p + 1)) != -1);
				//final long end = System.currentTimeMillis();

				//System.out.println("Calculated " + numPrimes + " in " + (end - start)/1000.0 + " seconds.\nWriting them to the file system now.");
				
				try(final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))){
					out.writeInt(numPrimes);
					out.writeInt(2);
					for(int i = 3; i < MAX; i += 2){
						if(!composites[i]){
							out.writeInt(i);
						}
					}
					
					
					out.flush();
					out.close();
				}catch(Exception e){
					e.printStackTrace();
					PRIMES = new int[numPrimes];
					int j = 0;
					PRIMES[j++] = 2;
					for(int i = 3; i < MAX; i += 2){
						if(!composites[i]){
							PRIMES[j++] = i;
						}
					}
				}

			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try(final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))){
			PRIMES = new int[in.readInt() - 1];
			for(int i = 0; i < PRIMES.length; i++){
				PRIMES[i] = in.readInt();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int search(boolean[] composites, int offset){
		for(int j = offset; j < composites.length; j++){
			if(!composites[j]){
				return j;
			}
		}
		return -1;
	}

	private Primes(){}

	public static int getNthPrime(int n){
		return PRIMES[n - 1]; //arrays start at 0 ^.^
	}

	public static int[] getAllPrimes(){
		return PRIMES.clone();
	}
	
	public static int[] getPrimes(int max){
		max++;
		int idx = Arrays.binarySearch(PRIMES, max);
		if(idx < 0)
			idx = ~idx;
		final int[] primes = new int[idx];
		System.arraycopy(PRIMES, 0, primes, 0, idx);
		return primes;
	}
	
	/**
	 * 
	 * @param primeNum
	 * @return If primeNum is a primeNum, it returns n so that the nth prime == primeNum
	 */
	public static int nthPrime(int primeNum){
		final int idx = Arrays.binarySearch(PRIMES, primeNum);
		return idx >= 0 ? idx + 1 : -1;
	}

	public static boolean isPrime(int num){
		return Arrays.binarySearch(PRIMES, num) >= 0;
	}
	
}
