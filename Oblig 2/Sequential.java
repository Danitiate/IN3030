/**
 * @author Daniel Dutta <danieldu@student.matnat.uio.no>
 * @date 2019.02.27
 */
 
class Sequential{
  int n, flag;
  public Sequential(int n, int flag){
    this.n = n;
    this.flag = flag; /*This variable drastically reduces lines used, as every
                       *algorithm can be fit into a single method
                       *
                       *1: SEQ_NOT_TRANSPOSED 2: SEQ_A_TRANSPOSED 3: SEQ_A_TRANSPOSED
                       */
  }

  public double[][] sequential(double[][] a, double[][] b){
      double[][] c = new double[n][n];
      for(int i=0;i<n;i++)
        for(int j=0;j<n;j++)
          for(int k=0;k<n;k++)
            if(flag == 0){
              c[i][j] += a[i][k] * b[k][j];
            }else if(flag == 1){
              c[i][j] += a[k][i] * b[k][j];
            }else{
              c[i][j] += a[i][k] * b[j][k];
            }
      return c;
    }
/*
    public double[][] sequentialTransA(double[][] a, double[][] b){
      double[][] c = new double[n][n];
      for(int i=0;i<n;i++)
        for(int j=0;j<n;j++)
          for(int k=0;k<n;k++)
            c[i][j] += a[k][i] * b[k][j];
      return c;
    }

    public double[][] sequentialTransB(double[][] a, double[][] b){
      double[][] c = new double[n][n];
      for(int i=0;i<n;i++)
        for(int j=0;j<n;j++)
          for(int k=0;k<n;k++)
            c[i][j] += a[i][k] * b[j][k];
      return c;
    }*/
}
