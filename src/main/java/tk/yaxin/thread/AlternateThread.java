package tk.yaxin.thread;


/**
 * 交替线程执行
 * @author Administrator
 *
 */
public class AlternateThread {

	public static void main(String[] args){
		byte[] aa = new byte[0];
		byte[] bb = new byte[0];
		
		Thread a = new Thread(new B(-1,bb,aa));
		a.start();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Thread b = new Thread(new B(0,aa,bb));
		b.start();
	}
	
}

/**
 * 主要的目的就是ThreadA->ThreadB->ThreadC->ThreadA循环执行三个线程。
 * 为了控制线程执行的顺序，那么就必须要确定唤醒、等待的顺序，
 * 所以每一个线程必须同时持有两个对象锁，才能继续执行。一个对象锁是prev，
 * 就是前一个线程所持有的对象锁。还有一个就是自身对象锁。主要的思想就是，
 * 为了控制执行的顺序，必须要先持有prev锁，也就前一个线程要释放自身对象锁，
 * 再去申请自身对象锁，两者兼备时打印，之后首先调用self.notify()释放自身对象锁，
 * 唤醒下一个等待线程，再调用prev.wait()释放prev对象锁，终止当前线程，
 * 等待循环结束后再次被唤醒
 * 
 * 交替线程执行
 * @author Administrator
 *
 */
class B implements Runnable{
	volatile int i=0;
	
	private byte[] pre;
	private byte[] self;
	
	public B(int i,byte[] pre,byte[] self){
		this.i=i;
		this.pre=pre;
		this.self=self;
	}

	public void run() {
		while(i<100){
			synchronized (pre) {
				i+=2;
				System.out.println(i);
				synchronized (self) {
					self.notifyAll();
				}
				try {
					pre.wait();//线程阻塞，停止在这里，后面的内容暂停
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		synchronized (self){
			self.notify();
		}
	}
	
}