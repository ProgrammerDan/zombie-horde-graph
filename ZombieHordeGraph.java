public class ZombieHordeGraph {
	int a=100,b,m,n,i,j,r,z,d,y,D,R,Z;
	float B,C,R,K;
	float p[][][];
	int o[];
	/**
	 * Initially I will read the input from STDIN.
	 * Format:
	 * m n
	 * from1 to1 z1
	 * ...
	 * fromN toN zN
	 * 
	 * 1 < m <= 100
	 * 1 < n <= 500
	 */
	ZombieHordeGraph() {
		Scanner in = Scanner(System.in);
		m = in.nextInt();
		n = in.nextInt();
		p[][][] = new float[m+1][m+1][n+1];
		o[] = new int[m+1];
		for (b=0;b<n;b++){
			i = in.nextInt();
			j = in.nextInt();
			z = in.nextInt();
			p[i][j][++p[i][j][0]]=z;
			if (i!=j)
				p[j][i][++p[j][i][0]]=z;
		}
		m++; // "base 1ify"
	}

	void game() {
		// start at 1.
		D=1;z=0
		do{
			System.out.printf("%i - ",D);
			incOutposts();
			int[]Q=pickDestinationArg1(D);
			Z=p[D][Q[0]][Q[1]];//#zombies on selected route
			z+=(a>Z)?Z:a;//zombies killed
			a=a+o[Q[0]]-Z;//ending ammunition
			R=R+(o[Q[0]]-Z-R)/++K;//running average of ammunition gain
			D=Q[0];//move to outpost
			o[D]=0;//reset outpost
			System.out.printf("%i : %i : %i : %5.2f\n",D,z,a,R);
		}while(a>=0&&R<0f);
	}

	void incOutposts(){
		for (b=1;b<m;b++)
			o[b]++;
	}

	/**
	 * Maximize zombies killed vs ammunition gain in a single step.
	 */
	int[] pickDestinationAlg1(int s){
		n=y=0;B=C=0f;
		for(d=1;d<m;d++) // Try every destination
			for(r=1;r<=p[s][d][0];r++){ // Try every route
				C=p[s][d][r]/(o[d]+1f);
				if (C>B){
					B=C;n=d;y=r;
				}
			}
		return new int[]{n,y};
	}
}
