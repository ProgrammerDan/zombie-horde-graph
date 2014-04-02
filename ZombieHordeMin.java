import java.util.*;
public class ZombieHordeMin {
	int a=100,b,m,n,i,j,r,z,d,y,D,R,Z,N;
	int p[][][];
	Scanner in;
	Runtime rt;
	int[][] routes;
	int pewpew;
	int diedie;
	int[][] bestDeadRoute;
	int winwin;
	int[][] bestWinRoute;
	int fastforward[][];
	int ff;
	boolean ffOn;
	public static void main(String[]a){
		(new ZombieHordeMin()).pickRoute();
	}
	ZombieHordeMin() {
		in = new Scanner(System.in);
		rt = Runtime.getRuntime();
		m = in.nextInt();
		N = in.nextInt();
		p = new int[m+1][m+1][N+1];
		for (b=0;b<N;b++){
			i = in.nextInt();
			j = in.nextInt();
			z = in.nextInt();
			p[i][j][++p[i][j][0]]=z;
			if (i!=j)
				p[j][i][++p[j][i][0]]=z;
		}
		m++;
	}
	void pickRoute() {
		routes = new int[5000][m+3];
		routes[0][0]=a;
		Arrays.fill(routes[0],1,m,1);
		routes[0][m]=1;
		routes[0][m+1]=0;
		routes[0][m+2]=0;
		winwin=-1;pewpew=diedie=0;
		pickRoute(5000);
	}
	void pickRoute(int absoluteMaxDepth){
		fastforward = new int[N][];
		ff=0;
		ffOn=true;
		for (int mD = 1; mD <= absoluteMaxDepth; mD++) {
			System.out.printf("Checking len %d\n", mD);
			int k = ffRoute(0,mD);
			if (winwin>-1){
				System.out.printf("%d x\n", winwin);
				for (int win=0; win<=winwin;win++)
					System.out.printf(" %d:%d,%d-%d",win,bestWinRoute[win][0],bestWinRoute[win][1],bestWinRoute[win][2]);
				System.out.println();
				break;
			}
			if (k > 0) {
				System.out.printf("dead max %d kills, %d steps\n", pewpew, diedie);
				for (int die=0; die<=diedie;die++)
					System.out.printf(" %d:%d,%d-%d",die,bestDeadRoute[die][0],bestDeadRoute[die][1],bestDeadRoute[die][2]);
				System.out.println();
				break;
			}
		}
	}
	int ffRoute(int depth, int maxDepth){
		if (ff==0)
			return pickRoute(depth, maxDepth);
		int kk=0;
		int fm=ff;
		if (ffOn&&N*fm>rt.maxMemory()/(fastforward[0][0]*8+12))
			ffOn=false;
		int[][] fastmovement = fastforward;
		if (ffOn){
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
	int pickRoute(int depth, int maxDepth){
		if (depth==maxDepth)
			return 0;
		int routesTested = 0;
		int deathCount = 0;
		int source = routes[depth][m];
		int startammo = routes[depth][0];
		for(int dest=1;dest<m;dest++){
			for (int route=1;route<=p[source][dest][0];route++){
				routesTested++;
				routes[depth+1][0]=startammo-p[source][dest][route]+routes[depth][dest];
				for (int cp=1;cp<m;cp++)
					routes[depth+1][cp]=(dest==cp?1:routes[depth][cp]+1);
				routes[depth+1][m]=dest;
				routes[depth+1][m+1]=route;
				routes[depth+1][m+2]=routes[depth][m+2]+p[source][dest][route];
				if (startammo-p[source][dest][route]<1){
					deathCount++; 
					if (pewpew<routes[depth][m+2]+startammo){
						pewpew=routes[depth][m+2]+startammo;
						diedie=depth+1;
						bestDeadRoute=new int[depth+2][3];
						for (int copy=0;copy<=depth+1;copy++){
							bestDeadRoute[copy][0]=routes[copy][m];
							bestDeadRoute[copy][1]=routes[copy][m+1];
							bestDeadRoute[copy][2]=routes[copy][0];
						}
					}
				} else if (startammo-p[source][dest][route]+routes[depth][dest]>=startammo+p[source][dest][route]) {
					int checkStartAmmo=startammo-p[source][dest][route]+routes[depth][dest];
					int countChecks=0;
					int[] dCheck = new int[m];
					for (int check=depth;check>=0;check--){
						if (routes[check][m]==dest){
							int fDepth = depth + 1 - check;
							int followRoute=check+1;
							for (int cM=0;cM<m+3;cM++)
								routes[depth+2][cM]=routes[depth+1][cM];
							for (;followRoute<=depth; followRoute++){
								routes[depth+2][0]=routes[depth+2][0]
										-p[routes[depth+2][m]][routes[followRoute][m]][routes[followRoute][m+1]]
										+routes[depth+2][routes[followRoute][m]];
								for (int cp=1;cp<m;cp++)
									routes[depth+2][cp]=(routes[followRoute][m]==cp?1:routes[depth+2][cp]+1);
								routes[depth+2][m+2]=routes[depth+2][m+2]+p[routes[depth+2][m]][routes[followRoute][m]][routes[followRoute][m+1]];
								routes[depth+2][m]=routes[followRoute][m];
								routes[depth+2][m+1]=routes[followRoute][m+1];
							}
							if (followRoute==depth+1&&routes[depth+2][0]>=routes[depth+1][0]){
								winwin=depth+1;
								bestWinRoute=new int[depth+2][3];
								for (int copy=0;copy<depth+2;copy++){
									bestWinRoute[copy][0]=routes[copy][m];
									bestWinRoute[copy][1]=routes[copy][m+1];
									bestWinRoute[copy][2]=routes[copy][0];
								}
								return 0;
							}
						}
					}
					deathCount+=pickRoute(depth+1, maxDepth);
					if (winwin>-1)
						return 0;
				}else {
					deathCount+=pickRoute(depth+1, maxDepth);
					if (winwin>-1)
						return 0;
				}
				for (int cp=0;cp<m+3;cp++)
					routes[depth+1][cp]=0;
			}
		}
		if (routesTested==deathCount)
			return 1;
		else {
			if (ffOn&&depth == maxDepth-1) 
				fastforward[ff++]=compress(depth);
			return 0;
		}
	}
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
}
