package p12.exercise;

import java.util.*;


public class Test {

    public static void main(String[] args){
        Test test = new Test();
        test.testBasic();
        test.testEnqueue();
        test.testDequeue();
        test.testFullDequeue();
        test.testDequeueOneFromAll();
        test.testCloseAndReallocate();
        test.testExceptions();
        System.out.println("All tests passed");
    }

    private static void assertEquals(Object result, Object expected){
        if (!Objects.equals(result, expected)){
            System.out.println(result+" is different from "+expected);
            fail("generic error");
        }
    }

    private static void fail(String s){
        throw new RuntimeException(s);
    }

    public void testBasic() {
        // Creo le code Q1 e Q2, e metto un elemento in Q2 
        MultiQueue<Integer,String> mq = new MultiQueueImpl<>();
        assertEquals(mq.availableQueues().size(),0);
        mq.openNewQueue("Q1");
        mq.openNewQueue("Q2");
        assertEquals(mq.availableQueues(),Set.of("Q1","Q2"));
        mq.enqueue(1000, "Q2");
        // Verifico quali code sono vuote
        assertEquals(mq.isQueueEmpty("Q1"), true);
        assertEquals(mq.isQueueEmpty("Q2"), false);
    }
    
    public void testEnqueue() {
        // Creo le code Q1 e Q2, e ci metto dentro vari elementi 
        MultiQueue<Integer,String> mq = new MultiQueueImpl<>();
        assertEquals(mq.allEnqueuedElements().size(),0);
        mq.openNewQueue("Q1");
        mq.openNewQueue("Q2");
        mq.enqueue(1000, "Q2");
        mq.enqueue(1001, "Q2");
        mq.enqueue(1002, "Q2");
        mq.enqueue(1003, "Q1");
        mq.enqueue(1004, "Q1");
        // Verifico quali elementi sono complessivamente in coda
        assertEquals(mq.allEnqueuedElements(),Set.of(1000,1001,1002,1003,1004));
    }
    
    public void testDequeue() {
        // Creo le code Q1 e Q2, e ci metto dentro vari elementi 
        MultiQueue<Integer,String> mq = new MultiQueueImpl<>();
        assertEquals(mq.allEnqueuedElements().size(),0);
        mq.openNewQueue("Q1");
        mq.openNewQueue("Q2");
        mq.enqueue(1000, "Q2");
        mq.enqueue(1001, "Q2");
        mq.enqueue(1002, "Q2");
        mq.enqueue(1003, "Q1");
        mq.enqueue(1004, "Q1");
        // Verifico l'ordine di rimozione degli elementi
        assertEquals(mq.dequeue("Q1"),1003);
        assertEquals(mq.dequeue("Q2"),1000);
        assertEquals(mq.dequeue("Q2"),1001);
        assertEquals(mq.dequeue("Q2"),1002);
        assertEquals(mq.dequeue("Q2"),null);
        assertEquals(mq.dequeue("Q2"),null);
        // Altre aggiunte e rimozioni..
        mq.enqueue(1005, "Q1");
        mq.enqueue(1006, "Q2");
        assertEquals(mq.dequeue("Q2"),1006);
        assertEquals(mq.allEnqueuedElements(),Set.of(1004,1005));        
    }
    

    public void testFullDequeue() {
        // Creo le code Q1 e Q2, e ci metto dentro vari elementi 
        MultiQueue<Integer,String> mq = new MultiQueueImpl<>();
        assertEquals(mq.allEnqueuedElements().size(),0);
        mq.openNewQueue("Q1");
        mq.openNewQueue("Q2");
        mq.enqueue(1000, "Q2");
        mq.enqueue(1001, "Q2");
        mq.enqueue(1002, "Q2");
        mq.enqueue(1003, "Q1");
        mq.enqueue(1004, "Q1");
        // Rimuovo tutti gli elementi da una coda
        assertEquals(mq.dequeueAllFromQueue("Q2"), List.of(1000,1001,1002));
        assertEquals(mq.dequeueAllFromQueue("Q2").size(),0);
    }
    
    public void testDequeueOneFromAll() {
        // Creo le code Q1 e Q2 e Q3, e metto in Q1 e Q2 vari elementi 
        MultiQueue<Integer,String> mq = new MultiQueueImpl<>();
        assertEquals(mq.allEnqueuedElements().size(),0);
        mq.openNewQueue("Q1");
        mq.openNewQueue("Q2");
        mq.openNewQueue("Q3"); 
        mq.enqueue(1000, "Q2");
        mq.enqueue(1001, "Q2");
        mq.enqueue(1002, "Q2");
        mq.enqueue(1003, "Q1");
        mq.enqueue(1004, "Q1");
        // Rimuovo un elemento da ogni coda
        Map<String,Integer > map = mq.dequeueOneFromAllQueues();
        assertEquals(map.size(), 3);
        assertEquals(map.get("Q1"), 1003);
        assertEquals(map.get("Q2"), 1000);
        assertEquals(map.get("Q3"), null);
    }
    
    public void testCloseAndReallocate() {
        // Creo le code Q1 e Q2 e Q3, e ci metto dentro vari elementi 
        MultiQueue<Integer,String> mq = new MultiQueueImpl<>();
        assertEquals(mq.allEnqueuedElements().size(),0);
        mq.openNewQueue("Q1");
        mq.openNewQueue("Q2");
        mq.openNewQueue("Q3"); 
        mq.enqueue(1000, "Q2");
        mq.enqueue(1001, "Q2");
        mq.enqueue(1002, "Q2");
        mq.enqueue(1003, "Q1");
        mq.enqueue(1004, "Q1");
        mq.enqueue(1005, "Q3");
        assertEquals(mq.availableQueues(),Set.of("Q1","Q2","Q3"));
        assertEquals(mq.allEnqueuedElements(),Set.of(1000,1001,1002,1003,1004,1005));    
        // Chiudo Q1, i suoi elementi devono finire in qualche altra coda
        mq.closeQueueAndReallocate("Q1");
        assertEquals(mq.allEnqueuedElements(),Set.of(1000,1001,1002,1003,1004,1005));
        assertEquals(mq.availableQueues(),Set.of("Q2","Q3"));
    }
    
    public void testExceptions() {
        // Testo e le varie eccezioni
        MultiQueue<Integer,String> mq = new MultiQueueImpl<>();
        assertEquals(mq.allEnqueuedElements().size(),0);
        mq.openNewQueue("Q1");
        mq.openNewQueue("Q3");
        mq.enqueue(1000, "Q1");
        try{
            mq.openNewQueue("Q1");
            fail("can't open Q1 again");
        } catch (IllegalArgumentException e){}
          catch (Exception e){
            fail("wrong exception thrown");
        }
        try{
            mq.isQueueEmpty("Q2");
            fail("can't query a non-existing queue");
        } catch (IllegalArgumentException e){}
          catch (Exception e){
            fail("wrong exception thrown");
        }
        try{
            mq.enqueue(100,"Q2");
            fail("can't add into a non-existing queue");
        } catch (IllegalArgumentException e){}
          catch (Exception e){
            fail("wrong exception thrown");
        }
        try{
            mq.dequeue("Q2");
            fail("can't remove from a non-existing queue");
        } catch (IllegalArgumentException e){}
          catch (Exception e){
            fail("wrong exception thrown");
        }
        try{
            mq.dequeueAllFromQueue("Q2");
            fail("can't remove from a non-existing queue");
        } catch (IllegalArgumentException e){}
          catch (Exception e){
            fail("wrong exception thrown");
        }
        try{
            mq.closeQueueAndReallocate("Q2");
            fail("can't remove from a non-existing queue");
        } catch (IllegalArgumentException e){}
          catch (Exception e){
            fail("wrong exception thrown");
        }
        mq.closeQueueAndReallocate("Q3");
        // Ok, but now we have only Q1 left.
        try{
            mq.closeQueueAndReallocate("Q1");
            fail("can't close if there's no other queue");
        } catch (IllegalStateException e){}
          catch (Exception e){
            fail("wrong exception thrown");
        }
    }
}
