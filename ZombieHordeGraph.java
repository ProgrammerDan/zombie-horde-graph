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


	int[][] routes; //stack. stack pointer is passed to recursive frame.

	void pickRoute(int start) {
		routes = new int[5000][m+3];
		routes[0][0]=a; // ammo before moving
		System.arraycopy(o,1,routes[0],1,m-1); // ammo in outpost before moving
		routes[0][m]=0; // outpost to go to next
		routes[0][m+1]=0; // route to follow
		routes[0][m+2]=0; // zombies killed so far including # killed going to next outpost
		winwin = Integer.MAX_VALUE;
		pewpew=diedie=0;
		pickRoute(0);
		if(winwin<Integer.MAX_VALUE){
			System.out.println("Infinite Cycle found, perfect survival -- %d steps", winwin);
		}else{
			System.out.println("No perfect cycle found, best cycle results in %d kills in %d steps", pewpew, diedie);
		}
	}

	int pewpew; // best zombies killed route
	int diedie; // best dead route length
	int[][] bestDeadRoute;
	int winwin; // win route length
	int[][] bestWinRoute;

	/**
	 * Try to find a route that results in an loop where the ammo is greater than or equal to start
	 */
	void pickRoute(int depth){
		int source = (depth<1)?1:routes[depth-1][m];
		int startammo = (depth<1)?a:routes[depth-1][0];
		for(int dest=1;dest<m;dest++){
			for (int route=1;route<p[source][dest][0];route++){
				//enter -- push to stack
				routes[depth+1][0]=startammo-p[source][dest][route]+routes[depth][dest];
				for (int cp=1;cp<m;cp++)
					routes[depth+1][cp]=(dest==cp)?1:routes[depth][cp]+1;
				routes[depth+1][m]=dest;
				routes[depth+1][m+1]=route;
				routes[depth+1][m+2]=routes[depth][m+2]+p[source][dest][route];
				
				// evaluate for win/lose
				if (startammo-p[source][dest][route]<1){ // you die if you go here
					if (pewpew<routes[depth][m+2]+startammo){ // new best death path
						pewpew=routes[depth][m+2]+startammo;
						diedie=depth+1;
						bestDeadRoute=new int[depth+1][2];
						for (int copy=0;copy<depth+1;copy++){
							bestDeadRoute[copy][0]=routes[copy][m];
							bestDeadRoute[copy][1]=routes[copy][m+1];
						}
					}
				} else if (startammo-p[source][dest][route]+routes[depth][dest]>a) {// maybe win?
					// TODO check next steps to see if we can return to an earlier step of loop with equal or greater ammo in inventory and ammo in outpost.
					//int[] tW = pickDestinationAlg1(dest);
					//if (startammo-p[source][dest][route]+routes[depth][dest]-p[dest][tw[0]][tw[1]]+routes[depth+1][tw[0]]>a) {// next step still winning?
						if (depth+1 < winwin) { // quicker win!
							winwin=depth+1;
							bestWinRoute=new int[depth+1][2];
							for (int copy=0;copy<depth+1;copy++){
								bestWinRoute[copy][0]=routes[copy][m];
								bestWinRoute[copy][1]=routes[copy][m+1];
							}
						}
					}
				}else {
					// no death or win, move to next depth
					pickRoute(depth+1);
				}
				// exit -- pop from stack
				for (int cp=0;cp<m+3;cp++)
					routes[depth+1][cp]=0;
			}
		}
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
