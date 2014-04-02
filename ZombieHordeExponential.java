import java.util.Scanner;
public class ZombieHordeExponential {
	int a=100,b,m,n,i,j,r,z,d,y,D,R,Z,N;
	int p[][][];
	int o[];
	Scanner in;
	Runtime rt;
	public static void main(String[]a){
		(new ZombieHordeExponential()).pickRoute();
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
	ZombieHordeExponential() {
		in = new Scanner(System.in);
		rt = Runtime.getRuntime();
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

	int[][] routes; //stack. stack pointer is passed to recursive frame.

	void pickRoute() {
		System.out.println("Starting to pick a route!");
		for (int b=1;b<m;b++) // prep the outposts for our first move.
			o[b]++;
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
	long[] totalBranches;
	long[] deadBranches;
	long[] liveBranches;
	int fastforward[][];
	int ff;
	boolean ffOn;

	/** 
	 * IDDF approach to picking a route. Keep trying deeper depths until we find a solution.
	 */
	void pickRoute(int absoluteMaxDepth){
		fastforward = new int[N][];
		ff=0;
		ffOn=true;
		for (int mD = 1; mD <= absoluteMaxDepth; mD++) {
			System.out.printf("Testing all routes of length %d\n", mD);
			totalProcessTime=System.currentTimeMillis();
			totalRoutes=deadRoutes=winTestRoutes=leafCount=0l;
			liveBranches=new long[mD];
			deadBranches=new long[mD];
			totalBranches=new long[mD];
			int k = ffRoute(0,mD);
			System.out.printf("Tested %d routes, %d of which lead to death, %d led to momentary ammo increase, in %d ms. Ended with %d leaves and %d FFnodes.\n",
					totalRoutes,deadRoutes,winTestRoutes,System.currentTimeMillis()-totalProcessTime, leafCount, ff);
			//System.out.printf("dep: %10s %10s %10s\n","total","dead","live");
			//for(int qq=0;qq<mD;qq++)
			//	System.out.printf("%3d: %10d %10d %10d\n",qq,totalBranches[qq],deadBranches[qq],liveBranches[qq]);
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

	int ffRoute(int depth, int maxDepth){
		if (ff==0){
			return pickRoute(depth, maxDepth);
		}
		int kk=0;
		int fm=ff;
		if (ffOn&&N*fm>rt.maxMemory()/(fastforward[0][0]*8+12))
			ffOn=false;

		int[][] fastmovement = fastforward;

		if (ffOn){
			System.out.printf("Allocating %d ff entries as it is <= %d\n", N*fm, rt.maxMemory()/(fastforward[0][0]*8+12));
			fastforward = new int[N*fm][];
			ff=0;
		}

		for (int df=0;df<fm;df++){
			decompressSparse(fastmovement[df]);
			kk+=pickRoute(fastmovement[df][0],maxDepth);
		}
		fastmovement=null;
		rt.gc();
		return kk==fm?1:0;
	}

	/**
	 * Try to find a route that results in an loop where the ammo is greater than or equal to start.
	 * We will use an IDDF approach.
	 */
	int pickRoute(int depth, int maxDepth){
		if (depth==maxDepth) {// IDDF Cap.
			leafCount++; // Currently I generate route compressions at maxDepth-1, I could generate them here instead and do +2 depth a turn... 
			return 0;
		}

		totalBranches[depth]++;

		long depthTime=System.currentTimeMillis();

		int routesTested = 0;
		int deathCount = 0;
		int source = routes[depth][m];
		int startammo = routes[depth][0];
		for(int dest=1;dest<m;dest++){
			//System.out.printf("Looking at dest %d", dest);
			for (int route=1;route<=p[source][dest][0];route++){
				//System.out.printf(" and route %d\n", route);
				//enter -- push to stack
				routesTested++;totalRoutes++;
				routes[depth+1][0]=startammo-p[source][dest][route]+routes[depth][dest];
				for (int cp=1;cp<m;cp++)
					routes[depth+1][cp]=(dest==cp?1:routes[depth][cp]+1);
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
					int checkStartAmmo=startammo-p[source][dest][route]+routes[depth][dest];
					int countChecks=0;
					int[] dCheck = new int[m];
					// Try to find a point in the current route that we can re-enter the route (a cycle)
					for (int check=depth;check>=0;check--){
						if (routes[check][m]==dest){ // we can start back into the route from this identical point.
							// prep top of the stack for a simple route follow.
							int fDepth = depth + 1 - check;
							int followRoute=check+1;
							for (int cM=0;cM<m+3;cM++) // copy prior stack frame forward to act as re-tread start
								routes[depth+2][cM]=routes[depth+1][cM];
							for (;followRoute<=depth; followRoute++){
								routes[depth+2][0]=routes[depth+2][0] // start with current ammo
										-p[ routes[depth+2][m] ][ routes[followRoute][m] ][ routes[followRoute][m+1] ] // subtract zombies on route taken
										+routes[depth+2][ routes[followRoute][m] ]; // add in ammo gained on arrival
								// copy forward rest of stuff, deal with ammo.
								for (int cp=1;cp<m;cp++)
									routes[depth+2][cp]=(routes[followRoute][m]==cp?1:routes[depth+2][cp]+1);
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
									return 0; // Short circuit, first shortest win is best.
								}
							}
						}
					}
					// Not a win, tried every possible legal cycle
					deathCount+=pickRoute(depth+1, maxDepth);
					if (winwin < Integer.MAX_VALUE)// short circuit on win found.
						return 0;
				}else {
					// no death or win, move to next depth
					deathCount+=pickRoute(depth+1, maxDepth);
					if (winwin < Integer.MAX_VALUE)// short circuit on win found.
						return 0;
				}
				// exit -- pop from stack
				for (int cp=0;cp<m+3;cp++)
					routes[depth+1][cp]=0;
			}
		}
		// Progress reporting for the impatient
		if (System.currentTimeMillis()-depthTime>2000L){
			System.out.print("1");
			for(int pR=1;pR<=depth;pR++)
				System.out.printf("-%d,%d",routes[pR][m+1],routes[pR][m]);
			System.out.printf("..so far %d routes, %d deadends, %d win tests - %d ms\n",
				totalRoutes, deadRoutes, winTestRoutes, System.currentTimeMillis()-depthTime);
		}
		if (routesTested==deathCount) { // all the routes we tested resulted in death. Earliest all-death branches are pruned.
			deadBranches[depth]++;
			return 1;
		} else {
			if (deathCount==0){
				liveBranches[depth]++;
			}
			// add to compressed list only if at from a final branch and we should recheck this later (e.g. not all deaths)
			// TODO: consider collecting compressed frames at leaves.
			if (ffOn&&depth == maxDepth-1) 
				addCompressed(compress(depth));
			return 0;
		}
	}
	void addCompressed(int[]cmp){
		fastforward[ff++]=cmp;
	}
	/**Compress. Basically, store depth, ending ammo and kills, and strictly which node was visited by what path along the way.
	 * We can compress depth*(m+3) to (depth*2+3), a significant compression:
	 * for m = 5:
	 * 1: 5/8 = 37.5% compression
	 * 2: 7/16 = 56.3%
	 * 3: 9/24 = 62.5%
	 * 4: 11/32 = 65.6%
	 * 5: 13/40 = 67.5%
	 * etc.
	 */
	int[]compress(int depth){
		int[]cmp=new int[depth*2+3];
		cmp[0]=depth;
		cmp[depth*2+1]=routes[depth][0];
		cmp[depth*2+2]=routes[depth][m+2];
		for(int zip=1;zip<=depth;zip++){
			cmp[zip]=routes[zip][m];
			cmp[depth+zip]=routes[zip][m+1];
		}
		return cmp;
	}
	/**Decompress but does not fill route ammo tables completely, only for the route taken.*/
	void decompressSparse(int[]cmp){
		int[]lv=new int[m];
		int depth=cmp[0];
		routes[depth][0]=cmp[depth*2+1];
		routes[depth][m+2]=cmp[depth*2+2];
		routes[0][0]=100;
		routes[0][m]=1;
		for(int dp=1;dp<=depth;dp++){
			routes[dp][m]=cmp[dp];
			routes[dp][m+1]=cmp[depth+dp];
			routes[dp-1][cmp[dp]]=dp-lv[cmp[dp]];
			routes[dp][m+2]=routes[dp-1][m+2]+p[routes[dp-1][m]][cmp[dp]][cmp[depth+dp]];
			routes[dp][0]=routes[dp-1][0]+routes[dp-1][cmp[dp]]-p[routes[dp-1][m]][cmp[dp]][cmp[depth+dp]];
			lv[cmp[dp]]=dp;
		}
		for(int am=1;am<m;am++)
			routes[depth][am]=(am==cmp[depth]?1:depth-lv[am]+1);
	}
	/**Utility Function to compare original with compressed, used it to prove correctness*/
	void compare(int depth){
		int[] cmp = compress(depth);
		System.out.println("original");
		for (int b=0;b<=m+2;b++){
			for (int a=0;a<=depth;a++){
				System.out.printf(" %3d",routes[a][b]);
				routes[a][b]=0;//clear
			}
			System.out.println();
		}
		decompressSparse(cmp);
		System.out.println("decompressed");
		for (int b=0;b<=m+2;b++){
			for (int a=0;a<=depth;a++){
				System.out.printf(" %3d",routes[a][b]);
			}
			System.out.println();
		}
	}

}
