/*
 * Original code:
 * Copyright © 2000–2017, Robert Sedgewick and Kevin Wayne.
 * <p>
 * Modifications:
 * Copyright (c) 2017. Phasmid Software
 */
package edu.neu.coe.info6205.union_find;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Height-weighted Quick Union with Path Compression
 */
public class UF_HWQUPC implements UF {
    /**
     * Ensure that site p is connected to site q,
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     */
    public void connect(int p, int q) {
        if (!isConnected(p, q)){union(p, q);}
        //else{System.out.println("already connected"+p+"and"+q);}
    }

    /**
     * Initializes an empty union–find data structure with {@code n} sites
     * {@code 0} through {@code n-1}. Each site is initially in its own
     * component.
     *
     * @param n               the number of sites
     * @param pathCompression whether to use path compression
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public UF_HWQUPC(int n, boolean pathCompression) {
        count = n;
        parent = new int[n];
        height = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            height[i] = 1;
        }
        this.pathCompression = pathCompression;
    }

    /**
     * Initializes an empty union–find data structure with {@code n} sites
     * {@code 0} through {@code n-1}. Each site is initially in its own
     * component.
     * This data structure uses path compression
     *
     * @param n the number of sites
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public UF_HWQUPC(int n) {
        this(n, true);
    }

    public void show() {
        for (int i = 0; i < parent.length; i++) {
            System.out.printf("%d: %d, %d\n", i, parent[i], height[i]);
        }
    }

    /**
     * Returns the number of components.
     *
     * @return the number of components (between {@code 1} and {@code n})
     */
    public int components() {
        return count;
    }

    /**
     * Returns the component identifier for the component containing site {@code p}.
     *
     * @param p the integer representing one site
     * @return the component identifier for the component containing site {@code p}
     * @throws IllegalArgumentException unless {@code 0 <= p < n}
     */
    //get the root for passed value, if pathcompression enabled,point the value directly to the root
    public int find(int p) {
        validate(p);
        int root = p;
        while(root!=getParent(root)){
            root=parent[root];
        }
        if(pathCompression){doPathCompression(p);}
        return root;

    }

    /**
     * Returns true if the the two sites are in the same component.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @return {@code true} if the two sites {@code p} and {@code q} are in the same component;
     * {@code false} otherwise
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    /**
     * Merges the component containing site {@code p} with the
     * the component containing site {@code q}.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public void union(int p, int q) {
        // CONSIDER can we avoid doing find again? //yes,pass the roots instead of values
        mergeComponents(find(p), find(q));
        //System.out.println("conncting"+p+"and"+q);
        count--;
    }

    @Override
    public int size() {
        return parent.length;
    }

    /**
     * Used only by testing code
     *
     * @param pathCompression true if you want path compression
     */
    public void setPathCompression(boolean pathCompression) {
        this.pathCompression = pathCompression;
    }

    @Override
    public String toString() {
        return "UF_HWQUPC:" + "\n  count: " + count +
                "\n  path compression? " + pathCompression +
                "\n  parents: " + Arrays.toString(parent) +
                "\n  heights: " + Arrays.toString(height);
    }

    // validate that p is a valid index
    private void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n - 1));
        }
    }

    private void updateParent(int p, int x) {
        parent[p] = x;
    }

    private void updateHeight(int p, int x) {
        height[p] += height[x];
    }

    /**
     * Used only by testing code
     *
     * @param i the component
     * @return the parent of the component
     */
    private int getParent(int i) {
        return parent[i];
    }

    private final int[] parent;   // parent[i] = parent of i
    private final int[] height;   // height[i] = height of subtree rooted at i
    private int count;  // number of components
    private boolean pathCompression;

    private void mergeComponents(int i, int j) {
        // TO BE IMPLEMENTED make shorter root point to taller one
        int rooti= find(i);
        int rootj=find(j);
        int height1=height[rooti];
        int height2=height[rootj];
        //update height later ??
        if(height1>=height2){updateParent(rootj,rooti); updateHeight(rooti,rootj);}
        else {updateParent(rooti,rootj); updateHeight(rootj,rooti);}

    }

    /**
     * This implements the single-pass path-halving mechanism of path compression
     */
    private void doPathCompression(int i) {
        // TO BE IMPLEMENTED update parent to value of grandparent
        validate(i);
        setPathCompression(false);
        updateParent(i,find(i));
        setPathCompression(true);

    }

    public static int count(int n,boolean boolval) {
        int connections = 0;
        UF_HWQUPC test;
        Random rand = new Random();
        if(boolval)test =new UF_HWQUPC(n,true);
        else{test =new UF_HWQUPC(n);}
        //till there is only 1 component left
        while (test.count > 1) {
            int number1 = rand.nextInt(n);
            int number2 = rand.nextInt(n);
            while (number1 == number2) {number1 = rand.nextInt(n);}
            test.connect(number1, number2);
            connections++;
        }
        return connections;
    }

    //The below is for running the experiment by taking inputs from cmdline
/*
    public static void main(String[] args){
        if (args.length < 1)
            throw new RuntimeException("Enter atleast 1 input,one for components and other for enabling path compression");
        //passed arg[0]  via cmdline;
        int n = Integer.parseInt(args[0]);
        if (n < 2) throw new IllegalArgumentException();
        boolean boolval;
        if(args.length<3&&Integer.parseInt(args[1])==1){boolval=true; count(n,true);} else{boolval=false;count(n,false);}
        int[] edges = new int[10];
        //running the experiment for 10 trials to get avg value
        for (int t = 0; t < 10; t++) {
            edges[t] = count(n,boolval);
        }
        int sum=0;
        for (int t = 0; t < 10; t++) {
            sum = sum+edges[t];
        }
        int average=sum/10;
        System.out.println("for"+n+"values:"+average);

}*/

//run this to get tries for different values of n=100,200,400.... and plot the graph
    public static void main(String[] args){
        int n=100;
        //run for diffrent values of n
        for(int i=0;i<10;i++){
            boolean boolval;
            //in count ,create the object and return number of connections needed to make the number of components as 1
            count(n,true);
            int[] edges = new int[10];
            //running the experiment for 10 trials to get avg value
            for (int t = 0; t < 10; t++) {
                edges[t] = count(n,true);  //setting path compression to true always(else uncomment the previous main method() and run)
            }
            int sum=0;
            for (int t = 0; t < 10; t++) {
                sum = sum+edges[t];
            }
            int average=sum/10;
            System.out.println("for"+n+"values:"+average);
            n=n*2;
        }


    }

}





