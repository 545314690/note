package code.threadlocal;



public class SequenceNumber {
 private static ThreadLocal<Integer> seqNum = new ThreadLocal<Integer>(){  
  public Integer initialValue(){  
   return 0;  
  }  
 };  
   
 public int getNextNum(){  
  seqNum.set(seqNum.get() + 1);  
  return seqNum.get();  
 }  
   
 public static void main(String[] args){  
  SequenceNumber sn = new SequenceNumber();  
  TestClient t1  = new TestClient(sn);  
  TestClient t2  = new TestClient(sn);  
  TestClient t3  = new TestClient(sn);  
    
  t1.start();  
  t2.start();  
  t3.start();  
    
  t1.print();  
  t2.print();  
  t3.print();   
 }  
   
 private static class TestClient extends Thread{  
  private SequenceNumber sn ;  
  public TestClient(SequenceNumber sn ){  
   this.sn = sn;  
  }  
    
  public void run(){  
   for(int i=0; i< 3; i++){  
    System.out.println( Thread.currentThread().getName()  + " --> " + sn.getNextNum());  
   }  
  }  
    
  public void print(){  
   for(int i=0; i< 3; i++){  
    System.out.println( Thread.currentThread().getName()  + " --> " + sn.getNextNum());  
   }  
  }  
 }  
} 