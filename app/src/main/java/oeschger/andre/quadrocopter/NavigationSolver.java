package oeschger.andre.quadrocopter;

import oeschger.andre.quadrocopter.util.ValuesStore;

/**
 * Created by andre on 16.01.16.
 */
public class NavigationSolver implements Runnable{



    private ValuesStore valuesStore;

    public NavigationSolver(ValuesStore valuesStore){
        this.valuesStore = valuesStore;
    }

    @Override
    public void run() {

    }
}
