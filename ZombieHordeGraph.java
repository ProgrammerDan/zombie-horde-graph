import java.util.Scanner;
public class ZombieHordeGraph {
	int a=100,b,m,n,i,j,r,z,d,y,D,R,Z,N;
	float B,C,W,K;
	int p[][][];
	int o[];
	Scanner in;
	public static void main(String[]a){
		//(new ZombieHordeGraph()).pickRoute();
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

	void pickRoute() {
		System.out.println("Starting to pick a route!");
		incOutposts(); // prep the outposts for our first move.
		routes = new int[5000][m+3];
		routes[0][0]=a; // ammo before moving
		System.arraycopy(o,1,routes[0],1,m-1); // ammo in outpost before moving
		routes[0][m]=1; // outpost to go to next
		routes[0][m+1]=0; // route to follow
		routes[0][m+2]=0; // zombies killed so far including # killed going to next outpost
		winwin = Integer.MAX_VALUE;
		pewpew=diedie=0;
		pickRoute(5000);
	}

	int pewpew; // best zombies killed route
	int diedie; // best dead route length
	int[][] bestDeadRoute;
	int winwin; // win route length
	int[][] bestWinRoute;
	long totalRoutes;
	long deadRoutes;
	long winTestRoutes;
	long totalProcessTime;
	long leafCount;
	int leaves[][][];
	int maxLeaf;

	/** 
	 * IDDF approach to picking a route. Keep trying deeper depths until we find a solution.
	 */
	void pickRoute(int absoluteMaxDepth){
		for (int mD = 1; mD <= absoluteMaxDepth; mD++) {
			System.out.printf("Testing all routes of length %d\n", mD);
			totalProcessTime=System.currentTimeMillis();
			totalRoutes=deadRoutes=winTestRoutes=leafCount=0l;
			int k = pickRoute(0,mD);
			System.out.printf("Tested %d routes, %d of which lead to death, %d led to momentary ammo increase, in %d ms. Ended with %d leaves.\n",
					totalRoutes,deadRoutes,winTestRoutes,System.currentTimeMillis()-totalProcessTime, leafCount);
			if (winwin<Integer.MAX_VALUE){
				System.out.printf("Cycle found, perfect survival -- %d steps\n", winwin);
				for (int win=0; win<=winwin;win++){
					System.out.printf(" %d:%d,%d-%d",win,bestWinRoute[win][0],bestWinRoute[win][1],bestWinRoute[win][2]);
				}
				System.out.println();
				break;
			}
			if (k > 0) {
				System.out.printf("All routes resolve to death. Best route: %d kills in %d steps\n", pewpew, diedie);
				for (int die=0; die<=diedie;die++){
					System.out.printf(" %d:%d,%d-%d",die,bestDeadRoute[die][0],bestDeadRoute[die][1],bestDeadRoute[die][2]);
				}
				System.out.println();
				break;
			}
		}
	}

	/**
	 * Try to find a route that results in an loop where the ammo is greater than or equal to start.
	 * We will use an IDDF approach.
	 */
	int pickRoute(int depth, int maxDepth){
		if (depth==maxDepth) {// IDDF Cap.
			leafCount++; // let's start to estimate the feasibility of branch pruning by starting the next IDDF round only at the prior reachable leaf nodes.
			return 0;
		}

		long depthTime=System.currentTimeMillis();

		int routesTested = 0;
		int deathCount = 0;
		int source = (depth<1)?1:routes[depth][m];
		int startammo = (depth<1)?a:routes[depth][0];
		for(int dest=1;dest<m;dest++){
			//System.out.printf("Looking at dest %d", dest);
			for (int route=1;route<=p[source][dest][0];route++){
				//System.out.printf(" and route %d\n", route);
				//enter -- push to stack
				routesTested++;totalRoutes++;
				routes[depth+1][0]=startammo-p[source][dest][route]+routes[depth][dest];
				for (int cp=1;cp<m;cp++)
					routes[depth+1][cp]=(dest==cp)?1:routes[depth][cp]+1;
				routes[depth+1][m]=dest;
				routes[depth+1][m+1]=route;
				routes[depth+1][m+2]=routes[depth][m+2]+p[source][dest][route];
				
				// evaluate for win/lose
				if (startammo-p[source][dest][route]<1){ // you die if you go here
					deathCount++; deadRoutes++;
					if (pewpew<routes[depth][m+2]+startammo){ // new best death path
						pewpew=routes[depth][m+2]+startammo;
						System.out.printf("Found death path in %d moves", depth);
						System.out.printf(" and it's a new best death path with %d kills", pewpew);
						diedie=depth+1;
						bestDeadRoute=new int[depth+2][3];
						for (int copy=0;copy<=depth+1;copy++){
							bestDeadRoute[copy][0]=routes[copy][m];
							bestDeadRoute[copy][1]=routes[copy][m+1];
							bestDeadRoute[copy][2]=routes[copy][0];
						}
						System.out.println();
					}
				} else if (startammo-p[source][dest][route]+routes[depth][dest]>=startammo+p[source][dest][route]) {// maybe win?
					winTestRoutes++;
					//System.out.printf("Found potential win path in %d moves: %d > %d", depth, startammo-p[source][dest][route]+routes[depth][dest],startammo);
					int checkStartAmmo=startammo-p[source][dest][route]+routes[depth][dest];
					int countChecks=0;
					int[] dCheck = new int[m];
					// Try to find a point in the current route that we can re-enter the route (a cycle)
					for (int check=depth;check>=0;check--){
						if (routes[check][m]==dest){ // we can start back into the route from this identical point.
							//System.out.printf(" try from move %d",check);
							// prep top of the stack for a simple route follow.
							int fDepth = depth + 1 - check;
							int followRoute=check+1;
							for (int cM=0;cM<m+3;cM++) // copy prior stack frame forward to act as re-tread start
								routes[depth+2][cM]=routes[depth+1][cM];
							for (;followRoute<=depth; followRoute++){
								routes[depth+2][0]=routes[depth+2][0] // start with current ammo
										-p[ routes[depth+2][m] ][ routes[followRoute][m] ][ routes[followRoute][m+1] ] // subtract zombies on route taken
										+routes[depth+2][ routes[followRoute][m] ]; // add in ammo gained on arrival
								if (routes[depth+2][0]<routes[followRoute+1][0]) {
									// if at any point in our retread we wind up with less any than we start with, game over man.
									//System.out.printf(" failed during move from %d to %d", followRoute, followRoute+1);
									break;
								}
								// copy forward rest of stuff, deal with ammo.
								for (int cp=1;cp<m;cp++)
									routes[depth+2][cp]=(routes[followRoute][m]==cp)?1:routes[depth+2][cp]+1;
								// don't need to track zombie kills, but whatever. Might as well.
								routes[depth+2][m+2]=routes[depth+2][m+2]+p[ routes[depth+2][m] ][ routes[followRoute][m] ][ routes[followRoute][m+1]];
								routes[depth+2][m]=routes[followRoute][m];
								routes[depth+2][m+1]=routes[followRoute][m+1];
							}
							if (followRoute==depth+1&&routes[depth+2][0]>=routes[depth+1][0]){ // we are back where we started after retracing, with equal or more ammo.
								System.out.printf("Found and confirmed win in %d moves by retracing full route starting with step %d", depth, check);
								if (depth+1 < winwin) { // quicker win!
									System.out.print(" and is new best win path!\n");
									winwin=depth+1;
									bestWinRoute=new int[depth+2][3];
									for (int copy=0;copy<depth+2;copy++){
										bestWinRoute[copy][0]=routes[copy][m];
										bestWinRoute[copy][1]=routes[copy][m+1];
										bestWinRoute[copy][2]=routes[copy][0];
									}
									return 0;
									// Short circuit, first shortest win is best.
								} // else should not happen as first win terminates TODO refactor to make this clearer.
							} else {
								//System.out.printf(" failed confirm %d==%d,%d>=%d",followRoute,depth+1,routes[depth+2][0],routes[depth+1][0]);
							}

							// not a win :(
						}
						// not yet a win, can't get back to the rest of the route yet.
					}
					//System.out.printf(" not a win, tried every possible legal cycle.\n");
					deathCount+=pickRoute(depth+1, maxDepth);
					if (winwin < Integer.MAX_VALUE)// short circuit on win found.
						return 0;
				}else {
					//System.out.printf("Branching from depth %d\n", depth);
					// no death or win, move to next depth
					deathCount+=pickRoute(depth+1, maxDepth);
					if (winwin < Integer.MAX_VALUE)// short circuit on win found.
						return 0;
				}
				// exit -- pop from stack
				for (int cp=0;cp<m+3;cp++)
					routes[depth+1][cp]=0;
			}
			//System.out.println();

		}
		if (System.currentTimeMillis()-depthTime>2000L){
			System.out.print("1");
			for(int pR=1;pR<=depth;pR++)
				System.out.printf("-%d,%d",routes[pR][m+1],routes[pR][m]);
			System.out.printf("..so far %d routes, %d deadends, %d win tests - %d ms\n",
				totalRoutes, deadRoutes, winTestRoutes, System.currentTimeMillis()-depthTime);
		}
		if (routesTested==deathCount) { // all the routes we tested resulted in death. TODO prune this for future IDDFs.
			//System.out.printf("Should prune a branch at depth %d", depth);
			return 1;
		} else {
			return 0;
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
