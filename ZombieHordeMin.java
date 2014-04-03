import java.util.*;
public class ZombieHordeMin {
	int a=100,b,m,n,i,j,z,y,D=0,R,Z,N;
	int p[][][];
	Scanner in;
	Runtime rt;
	int[][] r;
	int pp;
	int dd;
	int[][] bdr;
	int ww;
	int[][] bwr;
	int[][] faf;
	int ff;
	boolean ffOn;
	public static void main(String[]a){
		(new ZombieHordeMin()).pR();
	}
	ZombieHordeMin() {
		in = new Scanner(System.in);
		rt = Runtime.getRuntime();
		m = in.nextInt();
		N = in.nextInt();
		p = new int[m+1][m+1][N+1];
		int[]o=new int[m+1];
		for (b=0;b<N;b++){
			i = in.nextInt();
			j = in.nextInt();
			z = in.nextInt();
			o[i]++;
			o[j]++;
			D=(o[i]>D?o[i]:D);
			p[i][j][++p[i][j][0]]=z;
			if (i!=j)
				p[j][i][++p[j][i][0]]=z;
			D=(o[j]>D?o[j]:D);
		}
		m++;
	}
	void pR() {
		r = new int[5000][m+3];
		r[0][0]=a;
		Arrays.fill(r[0],1,m,1);
		r[0][m]=1;
		r[0][m+1]=0;
		r[0][m+2]=0;
		ww=-1;pp=dd=0;
		pR(5000);
	}
	void pR(int aMD){
		faf = new int[D][];
		ff=0;
		ffOn=true;
		for (int mD = 1; mD <= aMD; mD++) {
			System.out.printf("Checking len %d\n", mD);
			int k = ffRoute(0,mD);
			if (ww>-1){
				System.out.printf("%d x\n", ww+1);
				for (int win=0; win<=ww;win++)
					System.out.printf(" %d:%d,%d-%d",win,bwr[win][0],bwr[win][1],bwr[win][2]);
				System.out.println();
				break;
			}
			if (k > 0) {
				System.out.printf("dead max %d kills, %d steps\n", pp, dd+1);
				for (int die=0; die<=dd;die++)
					System.out.printf(" %d:%d,%d-%d",die,bdr[die][0],bdr[die][1],bdr[die][2]);
				System.out.println();
				break;
			}
		}
	}
	int ffRoute(int depth, int maxDepth){
		if (ff==0)
			return pR(depth, maxDepth);
		int kk=0;
		int fm=ff;
		if (ffOn&&D*fm>rt.maxMemory()/(faf[0][0]*8+12))
			ffOn=false;
		int[][] fmv = faf;
		if (ffOn){
			faf = new int[D*fm][];
			ff=0;
		}
		for (int df=0;df<fm;df++){
			decompressSparse(fmv[df]);
			kk+=pR(fmv[df][0],maxDepth);
		}
		fmv=null;
		rt.gc();
		return kk==fm?1:0;
	}
	int pR(int depth, int maxDepth){
		if (depth==maxDepth)
			return 0;
		int rTested = 0;
		int deathCount = 0;
		int source = r[depth][m];
		int startammo = r[depth][0];
		for(int dest=1;dest<m;dest++){
			for (int route=1;route<=p[source][dest][0];route++){
				rTested++;
				r[depth+1][0]=startammo-p[source][dest][route]+r[depth][dest];
				for (int cp=1;cp<m;cp++)
					r[depth+1][cp]=(dest==cp?1:r[depth][cp]+1);
				r[depth+1][m]=dest;
				r[depth+1][m+1]=route;
				r[depth+1][m+2]=r[depth][m+2]+p[source][dest][route];
				if (startammo-p[source][dest][route]<1){
					deathCount++; 
					if (pp<r[depth][m+2]+startammo){
						pp=r[depth][m+2]+startammo;
						dd=depth+1;
						bdr=new int[depth+2][3];
						for (int copy=0;copy<=depth+1;copy++){
							bdr[copy][0]=r[copy][m];
							bdr[copy][1]=r[copy][m+1];
							bdr[copy][2]=r[copy][0];
						}
					}
				} else {
					int countChecks=0;
					int[] dCheck = new int[m];
					for (int check=0;check<=depth;check++){
						if (r[check][m]==dest){
							int followRoute=check+1;
							for (int cM=0;cM<m+3;cM++)
								r[depth+2][cM]=r[depth+1][cM];
							for (;followRoute<=depth+1; followRoute++){
								r[depth+2][0]=r[depth+2][0]
										-p[r[depth+2][m]][r[followRoute][m]][r[followRoute][m+1]]
										+r[depth+2][r[followRoute][m]];
								for (int cp=1;cp<m;cp++)
									r[depth+2][cp]=(r[followRoute][m]==cp?1:r[depth+2][cp]+1);
								r[depth+2][m+2]=r[depth+2][m+2]+p[r[depth+2][m]][r[followRoute][m]][r[followRoute][m+1]];
								r[depth+2][m]=r[followRoute][m];
								r[depth+2][m+1]=r[followRoute][m+1];
							}
							if (followRoute==depth+2&&r[depth+2][0]>=r[depth+1][0]){
								ww=depth+1;
								bwr=new int[depth+2][3];
								for (int copy=0;copy<depth+2;copy++){
									bwr[copy][0]=r[copy][m];
									bwr[copy][1]=r[copy][m+1];
									bwr[copy][2]=r[copy][0];
								}
								return 0;
							}
						}
					}
					deathCount+=pR(depth+1, maxDepth);
					if (ww>-1)
						return 0;
				}
				for (int cp=0;cp<m+3;cp++)
					r[depth+1][cp]=0;
			}
		}
		if (rTested==deathCount)
			return 1;
		else {
			if (ffOn&&depth == maxDepth-1) 
				faf[ff++]=compress(depth);
			return 0;
		}
	}
	int[]compress(int depth){
		int[]cmp=new int[depth*2+3];
		cmp[0]=depth;
		cmp[depth*2+1]=r[depth][0];
		cmp[depth*2+2]=r[depth][m+2];
		for(int zip=1;zip<=depth;zip++){
			cmp[zip]=r[zip][m];
			cmp[depth+zip]=r[zip][m+1];
		}
		return cmp;
	}
	void decompressSparse(int[]cmp){
		int[]lv=new int[m];
		int depth=cmp[0];
		r[depth][0]=cmp[depth*2+1];
		r[depth][m+2]=cmp[depth*2+2];
		r[0][0]=100;
		r[0][m]=1;
		for(int dp=1;dp<=depth;dp++){
			r[dp][m]=cmp[dp];
			r[dp][m+1]=cmp[depth+dp];
			r[dp-1][cmp[dp]]=dp-lv[cmp[dp]];
			r[dp][m+2]=r[dp-1][m+2]+p[r[dp-1][m]][cmp[dp]][cmp[depth+dp]];
			r[dp][0]=r[dp-1][0]+r[dp-1][cmp[dp]]-p[r[dp-1][m]][cmp[dp]][cmp[depth+dp]];
			lv[cmp[dp]]=dp;
		}
		for(int am=1;am<m;am++)
			r[depth][am]=(am==cmp[depth]?1:depth-lv[am]+1);
	}
}
