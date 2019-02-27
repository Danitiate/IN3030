/**
 * @author Daniel Dutta <danieldu@student.matnat.uio.no>
 * @date 2019.02.27
 */

import java.util.concurrent.*;

class Parallell{
  int n, mode;
  double[][] a, b, c;
  int cores;
  CyclicBarrier finished;


  public Parallell(int n, double[][] a, double[][] b, int mode){
    this.n = n;
    this.a = a;
    this.b = b;
    this.mode = mode; //Flag used to determine what algorithm to use
    cores = Oblig2.NUM_CORES;
  }

  public double[][] solve(){
    Thread[] t = new Thread[cores];
    c = new double[n][n];
    for(int i = 0; i < cores; i++){
      t[i] = new Thread(new ParallellWorker(i));
      t[i].start();
    }
    for(int i = 0; i < cores; i++){
      try{ t[i].join();}catch (Exception e){return null;}
    }
    return c;
  }

  class ParallellWorker implements Runnable{
    int id;
    public ParallellWorker(int id){
      this.id = id;
    }

    public void run(){
      for(int i = id; i < n; i += cores){
        for(int j = 0; j < n; j++){
          for(int k = 0; k < n; k++){
            if(mode == 4) c[i][j] += a[i][k] * b[k][j];
            else if(mode == 5) c[i][j] += a[k][i] * b[k][j];
            else if(mode == 6) c[i][j] += a[i][k] * b[j][k];
          }
        }
      }
      /*switch(mode){
        case 4: parallell(); break;
        case 5: parallellTransA(); break;
        case 6: parallellTransB(); break;
      }*/
    }
    /*
    private void parallell(){
      for(int i = id; i < n; i += cores){
        for(int j = 0; j < n; j++){
          for(int k = 0; k < n; k++){
            c[i][j] += a[i][k] * b[k][j];
          }
        }
      }
    }

    private void parallellTransA(){
      for(int i = id; i < n; i += cores){
        for(int j = 0; j < n; j++){
          for(int k = 0; k < n; k++){
            c[i][j] += a[k][i] * b[k][j];
          }
        }
      }
    }

    private void parallellTransB(){
      for(int i = id; i < n; i += cores){
        for(int j = 0; j < n; j++){
          for(int k = 0; k < n; k++){
            c[i][j] += a[i][k] * b[j][k];
          }
        }
      }
    }*/
  }
}
