import java.util.concurrent.Semaphore;

class Account {
    String name;
    int balance;
    Semaphore lock = new Semaphore(1);

    Account(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }
}


class Transfer {

    static void transfer(Account from, Account to, int amount) {
        try {
            System.out.println(Thread.currentThread().getName() +
                    " trying to lock FROM " + from.name);
            from.lock.acquire();
            System.out.println(Thread.currentThread().getName() +
                    " locked FROM " + from.name);

            // Delay to increase deadlock chance
            Thread.sleep(100);

            System.out.println(Thread.currentThread().getName() +
                    " trying to lock TO " + to.name);
            to.lock.acquire();
            System.out.println(Thread.currentThread().getName() +
                    " locked TO " + to.name);

            // Critical section
            from.balance -= amount;
            to.balance += amount;

            System.out.println(Thread.currentThread().getName() +
                    " transfer completed");

            to.lock.release();
            from.lock.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


public class DeadlockSimulation {
    public static void main(String[] args) {
        System.out.println("Starting Deadlock Simulation...");
        Account account1 = new Account("Account-1", 1000);
        Account account2 = new Account("Account-2", 1000);

        Thread t1 = new Thread(() ->
                Transfer.transfer(account1, account2, 100),
                "Thread-1"
        );

        Thread t2 = new Thread(() ->
                Transfer.transfer(account2, account1, 200),
                "Thread-2"
        );

        t1.start();
        t2.start();
    }
}
