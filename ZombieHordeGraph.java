import java.util.Scanner;
public class ZombieHordeGraph {
	int a=100,b,m,n,i,j,r,z,d,y,D,R,Z,N;
	float B,C,W,K;
	int p[][][];
	int o[];
	Scanner in;
	public static void main(String[]a){
		(new ZombieHordeGraph()).game();
	}
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
		in = new Scanner(System.in);
		m = in.nextInt();
		N = in.nextInt();
		p = new int[m+1][m+1][N+1];
		o = new int[m+1];
		for (b=0;b<N;b++){
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
		D=1;z=0;
		do{
			//System.out.printf("%d - ",D);
			incOutposts();
			int[]Q=pickDestinationInt(D);
			Z=p[D][Q[0]][Q[1]];//#zombies on selected route
			z+=(a>Z)?Z:a;//zombies killed
			a=a+o[Q[0]]-Z;//ending ammunition
			W=W+(o[Q[0]]-Z-W)/++K;//running average of ammunition gain
			D=Q[0];//move to outpost
			o[D]=0;//reset outpost
			System.out.printf("%d : %d : %d : %5.2f\n",D,z,a,W);
		}while(a>=0&&W<0f);
	}

	void incOutposts(){
		for (b=1;b<m;b++)
			o[b]++;
	}

	/**
	 * Maximize ratio of ammunition gained over zombies killed in a single step.
	 */
	int[] pickDestinationAlg1(int s){
		n=y=0;B=C=0f;
		for(d=1;d<m;d++) // Try every destination
			for(r=1;r<=p[s][d][0];r++){ // Try every route
				C=(o[d]+1f)/p[s][d][r];
				if (C>B){
				//	System.out.printf("  - %d %d : %d : %.0f : %5.2f\n",
				//		d, r, p[s][d][r], o[d]+1f, C);
					B=C;n=d;y=r;
				}
			}
		return new int[]{n,y};
	}

	/**
	 * Minimize zombie kills from new ammo in a single step. Basically, preserve the most ammo.
	 */
	int[] pickDestinationAlg2(int s){
		n=y=0;B=C=Float.MAX_VALUE;
		for(d=1;d<m;d++) // Try every destination
			for(r=1;r<=p[s][d][0];r++){ // Try every route
				C=p[s][d][r]-(o[d]+1f);
				if (C<B){
				//	System.out.printf("  - %d %d : %d : %.0f : %5.2f\n",
				//		d, r, p[s][d][r], o[d]+1f, C);
					B=C;n=d;y=r;
				}
			}
		return new int[]{n,y};
	}

	/**
	 * Maximize ratio of ammunition gained over zombies killed in two steps.
	 */
	int[] pickDestinationAlg3(int s){
		int n,y;n=y=0;float B,C,A;B=C=0f;
		for(int d=1;d<m;d++){// Try every destination
			for(int r=1;r<=p[s][d][0];r++){ // Try every route
				for(int D=1;D<m;D++){// Try every destination
					for(int R=1;R<=p[s][d][0];R++){ // Try every route
						A=2f+((s==d)?0f:o[d])+((D==s)?d==s?0f:1f:o[D]);
						C=A/(p[s][d][r]+p[d][D][R]);
						if (C>B){
						//	System.out.printf("  - %d %d : %d : %.0f : %5.2f\n",
						//		d, r, p[s][d][r], o[d]+1f, C);
							B=C;n=d;y=r;
						}
					}
				}
			}
		}
		return new int[]{n,y};
	}

	/**
	 * Determine next route interactively.
	 */
	int[] pickDestinationInt(int s){
		int nc=0,nl=43,n,y;
		for(int d=1;d<m;d++){
			if (p[s][d][0]>0){
				System.out.printf("%3d %3d:", d, o[d]);
				for(int r=1;r<=p[s][d][0];r++){
					System.out.printf(" %3d z%3d",r,p[s][d][r]);
				}
				System.out.println();
			}
		}
		do{
			System.out.print("d?");
			n=in.nextInt();
			System.out.print("r?");
			y=in.nextInt();
		}while(n<1||n>m||y<1||y>N||p[s][n][y]<1);

		return new int[]{n,y};
	}

}
