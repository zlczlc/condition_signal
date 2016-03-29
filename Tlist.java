import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TList<T>{
	private LinkedList<T> list;
	private Lock lock;
	private Condition condition;
	public TList() {
		list = new LinkedList<T>();
		lock = new ReentrantLock();
		condition = lock.newCondition();
	}
	public void add(T t) {
		lock.lock();
		try {
			list.addLast(t);
			condition.signal();
		}
		finally {
			lock.unlock();
		}
		
	}
	public T get() {
		T t = null;
		lock.lock();
		try {
			while (list.size() == 0) {
				try {
					condition.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			t = list.removeFirst();
		}
		finally {
			lock.unlock();
		}
		return t;
	
	}
	
	public static void main(String[] args) {
		TList<Integer> list = new TList<>();
		Thread t1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("pick thread starts");
				System.out.println("got a number: " + list.get() + ", "+ this);
			}
			
		});
		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("adding thread starts");
				System.out.println("add 1 after 5s");
				try {
					Thread.sleep(5000);
				}
				catch (Exception e) {
					
				}
				list.add(1);
			}
			
		});
		t1.start();
		t2.start();
	}
}
