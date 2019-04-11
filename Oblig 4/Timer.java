class Timer{
  long start;
  long stop;

  public Timer(){
    this.start = System.nanoTime();
  }

  public void stop(){
    stop = System.nanoTime();
  }

  public long getRuntime(){
    return (stop-start)/1000000;
  }
}
