
/**
 * Created by 4 on 24.06.2016.
 */
class ThreadCheckUsers: Runnable {

    var thread: Thread;

    constructor(){
        thread = Thread(this, "ThreadCheckUsers");
        thread.start();
    }

    override fun run() {
        try {
            while(true){
                println(usersOnline.toString())
                if(usersOnline.size > 0){
                    for(i in 0..usersOnline.size - 1){
                        var differenceSeconds = (System.currentTimeMillis() - usersOnline[i].lastSeen)/1000L
                        if(differenceSeconds > 300){
                            usersOnline.remove(usersOnline[i])
                        }
                    }
                }
                Thread.sleep(60* 1000)//minute
            }
        } catch (e: InterruptedException) {
            println("thread checkUsers dead");
        }
    }
}