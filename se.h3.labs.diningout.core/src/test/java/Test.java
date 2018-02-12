import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Test {

    Random r = new Random();


    static class Node {
        List<Channel.ChannelEndPoint> channels = new ArrayList<Channel.ChannelEndPoint>();

        void connect(Channel.ChannelEndPoint endPoint) {
            channels.add(endPoint);
        }

        boolean connectsTo(final Node node) {

            for (Channel.ChannelEndPoint endPoint : channels) {
                if (endPoint.peer == node) return true;
            }

            return false;
        }
    }

    static class Worker extends Node {
        static List<Worker> workers = new ArrayList<Worker>();

        final Diner workplace;

        public Worker(Diner workplace) {
            this.workplace = workplace;

            new Channel(2000, 2000, workplace, this);
            workers.add(this);
        }
    }

    static class Diner extends Node {
        static List<Diner> diners = new ArrayList<Diner>();

        public Diner() {
            diners.add(this);
        }
    }

    static class Channel {
        final double capacity;
        double balance;

        public Channel(double capacity, double balance, Node sender, Node receiver) {
            this.capacity = capacity;
            this.balance = balance;

            sender.channels.add(new SenderEndPoint(receiver));
            receiver.channels.add(new ReceiverEndPoint(sender));
        }

        abstract class ChannelEndPoint {
            final Node peer;

            protected ChannelEndPoint(Node peer) {
                this.peer = peer;
            }

            abstract void send(double amount);

        }

        class SenderEndPoint extends ChannelEndPoint {
            SenderEndPoint(Node peer) {
                super(peer);
            }

            void send(double amount) {
                assert balance + amount <= capacity;
                balance += amount;
            }
        }

        class ReceiverEndPoint extends ChannelEndPoint {
            ReceiverEndPoint(Node peer) {
                super(peer);
            }

            void send(double amount) {

                assert balance - amount >= 0;
                balance -= amount;
            }
        }
    }




    @org.junit.Test
    public void testScenarion1() throws Exception {

        // Create 20 diners and 10 workers per diner

        for (int i = 0; i < 20; i++) {

            Diner d = new Diner();

            for (int j = 0; j < 10; j++) {
                new Worker(d);
            }
        }

        for (Worker w : Worker.workers) {
            for (int i = 0; i < 5; i++) {

                Worker peer;
                do {
                    peer = Worker.workers.get(r.nextInt(Worker.workers.size()));
                } while (peer == w || peer.connectsTo(w));

                new Channel(4000, 2000, peer, w);
            }
        }
    }
}
