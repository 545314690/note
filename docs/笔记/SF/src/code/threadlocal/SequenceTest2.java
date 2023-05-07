package code.threadlocal;



public class SequenceTest2 {
 static Integer value = 1000;
 private static ThreadLocal<Integer> seqNum = new ThreadLocal<Integer>();
// {
//  @Override
//  public Integer initialValue(){
//   return value;
//  }
// };

 public int getNextNum(){
  return seqNum.get();
 }  
   
 public static void main(String[] args){  
  SequenceTest2 sn = new SequenceTest2();
  TestClient t1  = new TestClient(sn);  
  TestClient t2  = new TestClient(sn);

  t1.start();
  t2.start();

//  t1.print();
  seqNum.set(value);
  for (int i = 0; i < 10; i++) {
   System.out.println( Thread.currentThread().getName()  + " --> " + seqNum.get());
   try {
    Thread.sleep(1);
   } catch (InterruptedException e) {
    e.printStackTrace();
   }
  }
 }

 private static class TestClient extends Thread{
  private SequenceTest2 sn ;
  public TestClient(SequenceTest2 sn){
   this.sn = sn;
  }

  public void run(){
   for(int i=0; i< 30; i++){
    value++;
    seqNum.set(value);
    System.out.println( Thread.currentThread().getName()  + " --> " + sn.getNextNum());
     try {
      Thread.sleep(2);
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
   }
  }

  public void print(){
   for(int i=0; i< 3; i++){
    System.out.println( Thread.currentThread().getName()  + " --> " + sn.getNextNum());
   }
  }
 }
}