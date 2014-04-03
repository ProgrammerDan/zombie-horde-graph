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
			int k = ffR(0,mD);
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
	int ffR(int dP, int mD){
		if (ff==0)
			return pR(dP, mD);
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
			dS(fmv[df]);
			kk+=pR(fmv[df][0],mD);
		}
		fmv=null;
		rt.gc();
		return kk==fm?1:0;
	}
	int pR(int dP, int mD){
		if (dP==mD)
			return 0;
		int rT = 0;
		int dC = 0;
		int src = r[dP][m];
		int sa = r[dP][0];
		for(int dt=1;dt<m;dt++){
			for (int rut=1;rut<=p[src][dt][0];rut++){
				rT++;
				r[dP+1][0]=sa-p[src][dt][rut]+r[dP][dt];
				for (int cp=1;cp<m;cp++)
					r[dP+1][cp]=(dt==cp?1:r[dP][cp]+1);
				r[dP+1][m]=dt;
				r[dP+1][m+1]=rut;
				r[dP+1][m+2]=r[dP][m+2]+p[src][dt][rut];
				if (sa-p[src][dt][rut]<1){
					dC++; 
					if (pp<r[dP][m+2]+sa){
						pp=r[dP][m+2]+sa;
						dd=dP+1;
						bdr=new int[dP+2][3];
						for (int cp=0;cp<=dP+1;cp++){
							bdr[cp][0]=r[cp][m];
							bdr[cp][1]=r[cp][m+1];
							bdr[cp][2]=r[cp][0];
						}
					}
				} else {
					for (int chk=0;chk<=dP;chk++){
						if (r[chk][m]==dt){
							int fR=chk+1;
							for (int cM=0;cM<m+3;cM++)
								r[dP+2][cM]=r[dP+1][cM];
							for (;fR<=dP+1; fR++){
								r[dP+2][0]=r[dP+2][0]-p[r[dP+2][m]][r[fR][m]][r[fR][m+1]]+r[dP+2][r[fR][m]];
								for (int cp=1;cp<m;cp++)
									r[dP+2][cp]=(r[fR][m]==cp?1:r[dP+2][cp]+1);
								r[dP+2][m+2]=r[dP+2][m+2]+p[r[dP+2][m]][r[fR][m]][r[fR][m+1]];
								r[dP+2][m]=r[fR][m];
								r[dP+2][m+1]=r[fR][m+1];
							}
							if (fR==dP+2&&r[dP+2][0]>=r[dP+1][0]){
								ww=dP+1;
								bwr=new int[dP+2][3];
								for (int cp=0;cp<dP+2;cp++){
									bwr[cp][0]=r[cp][m];
									bwr[cp][1]=r[cp][m+1];
									bwr[cp][2]=r[cp][0];
								}
								return 0;
							}
						}
					}
					dC+=pR(dP+1, mD);
					if (ww>-1)
						return 0;
				}
				for (int cp=0;cp<m+3;cp++)
					r[dP+1][cp]=0;
			}
		}
		if (rT==dC)
			return 1;
		else {
			if (ffOn&&dP == mD-1) 
				faf[ff++]=cP(dP);
			return 0;
		}
	}
	int[]cP(int dP){
		int[]cmp=new int[dP*2+3];
		cmp[0]=dP;
		cmp[dP*2+1]=r[dP][0];
		cmp[dP*2+2]=r[dP][m+2];
		for(int zip=1;zip<=dP;zip++){
			cmp[zip]=r[zip][m];
			cmp[dP+zip]=r[zip][m+1];
		}
		return cmp;
	}
	void dS(int[]cmp){
		int[]lv=new int[m];
		int dP=cmp[0];
		r[dP][0]=cmp[dP*2+1];
		r[dP][m+2]=cmp[dP*2+2];
		r[0][0]=100;
		r[0][m]=1;
		for(int dp=1;dp<=dP;dp++){
			r[dp][m]=cmp[dp];
			r[dp][m+1]=cmp[dP+dp];
			r[dp-1][cmp[dp]]=dp-lv[cmp[dp]];
			r[dp][m+2]=r[dp-1][m+2]+p[r[dp-1][m]][cmp[dp]][cmp[dP+dp]];
			r[dp][0]=r[dp-1][0]+r[dp-1][cmp[dp]]-p[r[dp-1][m]][cmp[dp]][cmp[dP+dp]];
			lv[cmp[dp]]=dp;
		}
		for(int am=1;am<m;am++)
			r[dP][am]=(am==cmp[dP]?1:dP-lv[am]+1);
	}
}
