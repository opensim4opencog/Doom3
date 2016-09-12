
import java.io.*; 
import java.awt.*; 
import java.util.*; 


//---------------------------------------------------------------------
//---------------------------------------------------------------------
//
//   The River World
//
//---------------------------------------------------------------------
//---------------------------------------------------------------------


class River implements State, Cloneable {
    private boolean same_side(Creature one, Creature two) {
        return one.which_side() == two.which_side();
    }

    public State get_initial() {
        River tempState = new River(false,false,false,false);
        return tempState; 
    }
    public State get_goal() {
        River tempState = new River(true,true,true,true);
        return tempState; 
    }

    public State get_copy() throws Exception
    {
        River temp = (River) this.clone();
        temp.theFarmer = this.theFarmer.get_copy();
        temp.theWolf = this.theWolf.get_copy();
        temp.theGoat = this.theGoat.get_copy();
        temp.theCabbage = this.theCabbage.get_copy();
        return temp;
    }


    public boolean equals(State s) {
        River r =  (River) s;
        return 
        (theWolf.which_side() == r.theWolf.which_side())
        && (theGoat.which_side() == r.theGoat.which_side())
        && (theCabbage.which_side() == r.theCabbage.which_side()) ;
    }


    public void apply(Object operator) throws Exception
    { 
        theFarmer.change_side();          //  the farmer always moves!
        if (((String) operator).compareTo("move_wolf") == 0)
            theWolf.change_side();
        else if (((String) operator).compareTo("move_goat") == 0)
            theGoat.change_side();
        else if (((String) operator).compareTo("move_cabbage") == 0)
            theCabbage.change_side();
    }

    public boolean legal_state() {
        if (same_side(theFarmer,theGoat))
            return true;  // if Farmer with goat, nothing can happen.
        else if (same_side(theWolf, theGoat ))
            return false;  // Wolf and Goat left alone ->  bloody mess
        else if (same_side(theGoat,theCabbage ))
            return false;  // Goat and cabbage left along -> bad
        else return true;
    }

    public Stack possible_operators() {
        Stack moves  = new Stack(); 
        moves.push("move_farmer");
        if (same_side(theFarmer,theWolf))
            moves.push("move_wolf");
        if (same_side(theFarmer,theGoat))
            moves.push("move_goat");
        if (same_side(theFarmer,theCabbage))
            moves.push("move_cabbage");
        return moves;
    }

    public Creature theFarmer;
    public Creature theWolf;
    public Creature theGoat;
    public Creature theCabbage;

    public static void main (String [] args) throws Exception 
    {}
    //  Constructor 
    public River() {
        theFarmer = new Creature("F");
        theWolf = new Creature("W");
        theGoat = new Creature("G");
        theCabbage = new Creature("C");
    }
    //  Constructor 
    public River(boolean f_side, boolean w_side, boolean g_side, boolean c_side) {
        theFarmer = new Creature("F");
        theWolf = new Creature("W");
        theGoat = new Creature("G");
        theCabbage = new Creature("C");
        theFarmer.set_side(f_side);
        theWolf.set_side(w_side);
        theGoat.set_side(g_side);
        theCabbage.set_side(c_side);
    }

    public void paint(Graphics window) {
        window.setColor(Color.cyan);
        window.fillRect(180,0,200,600);   // Paint river.
        theFarmer.paint(window);
        theWolf.paint(window);
        theGoat.paint(window);
        theCabbage.paint(window);
    }
}  // end of River class


class Creature implements Cloneable {
    private int center_x=150; int center_y;  
    private int width = 30; int height = 20; 
    private boolean side;
    private String species;

    public Creature(String input_species) {
        species = input_species;
        if (species.compareTo("W")==0)
            center_y=250;
        else if (species.compareTo("G")==0)
            center_y = 280;
        else if (species.compareTo("C")==0)
            center_y = 310;
        else center_y = 220;
    }

    public Creature get_copy() throws Exception
    {return(Creature) this.clone();}

    public void setCenterCoordinates(int new_x, int new_y) {
        center_x = new_x;
        center_y = new_y;
    }

    public boolean which_side() {
        return side;
    }
    public void change_side() {
        side = !side;
    }
    public void set_side(boolean input_side) {
        side = input_side;
    }

    public void paint(Graphics window) {
        int x = center_x;
        if (side) x+= 255;
        if (species.compareTo("W")==0)
            window.setColor(Color.black);
        else if (species.compareTo("G")==0)
            window.setColor(Color.red);
        else if (species.compareTo("C")==0)
            window.setColor(Color.green);
        else window.setColor(Color.blue);
        window.drawString(species,x-5,center_y+5);
        window.drawOval(x-(width/2),center_y - (height/2),width,height);

    }
}   // end of Creature class








//-------------------------------------------------------------------------
//-------------------------------------------------------------------------
//
//    PLANNER (the state space search engine) is below.
//
//-------------------------------------------------------------------------
//-------------------------------------------------------------------------

interface State {

    //  Essential methods:

    public State get_initial();
    public State get_goal();
    public State get_copy() throws Exception;
    public boolean equals(State s);
    public void apply(Object operator) throws Exception;  
    public boolean legal_state();
    public Stack possible_operators();
    public void paint(Graphics g);

    //  Nice-to-have's:

    //    public boolean valid_goal(State init, State goal);
    //    public void graph_plan(Plan solution);


}


class Plan extends Queue implements Cloneable {
    public Object popPlan() {
        return popQ();
    }
    public void pushPlan(Object operator) {
        pushQ(operator);
    }
    public Plan get_copy() {
        return(Plan) this.clone();
    }
    public void Print() {
        Plan local_plan = get_copy();
        System.out.println("\n A plan:  ");
        while (!local_plan.empty()) {
            String s = (String) local_plan.pop();
            System.out.println(" " + s);
        }
        System.out.println("\n");
    }
}


class Planner extends Frame {
    public Graphics g;
    public static Color background_color;

    State initial;
    State goal;
    State current_state;
    Queue plan_queue = new Queue();

    public static void main (String [] args) throws Exception 
    {
        River bloodRiver = new River();
        Planner DeepThought = new Planner(bloodRiver);
        Plan p = DeepThought.solve();
        p.Print();
        DeepThought.animatePlan(p);
    }

    public Planner(State s)  throws Exception           //  Constructor
    { 
        super(" Blood River  ");
        setSize(600,600);
        setBackground(Color.white);
        background_color = getBackground();
        setVisible(true);
        g = getGraphics();

        initial = s.get_initial();
        goal = s.get_goal(); 
        current_state = initial.get_copy();   // Initialize current state to copy of init.
    }

    public State apply_plan(Plan p, State s)  throws Exception
    {
        Plan localp =  p.get_copy();
        State temp_state = s.get_copy();
        int count = 1;
        while (!localp.empty()) {
            System.out.println("\n Number of ops in plan:  "+count);
            count++;
            temp_state.apply(localp.popPlan());
        }
        return temp_state;
    }   

    public Plan solve() throws Exception
    { 
        Plan p = new Plan();
        State s;
        Stack operators = new Stack();

        plan_queue.pushQ( p );

        boolean solved = false;

        while (!solved && !plan_queue.empty()) {
            p = (Plan) plan_queue.popQ();
            p.Print();
            s = apply_plan(p,initial);
            State temp_state;
            if (goal.equals(s)) {
                solved = true; System.out.println("\n Solved!\n");
                temp_state = s; paint(g); Thread.sleep(2000);
            } else {
                if (s.legal_state()) {
                    operators = s.possible_operators();
                    System.out.println("\n About to add children plans! ");
                    int i = 0;
                    while (!operators.empty()) {
                        Plan child_plan = p.get_copy();
                        child_plan.pushPlan( operators.pop() );
                        child_plan.Print();
                        plan_queue.pushQ(child_plan);
                        System.out.println(" " + i + " "); i++;
                    }
                }

            }
        }
        if (solved) {
            return p;
        } else return new Plan();
    }

    public void animatePlan(Plan p) throws Exception
    {
        current_state = initial.get_copy();
        paint(g);
        Thread.sleep(2000);      
        while (!p.empty()) {
            current_state.apply(p.popPlan()); 
            paint(g);
            System.out.println("\n  Here I am at ");
            Thread.sleep(2000);      
        }   
    }


    public void paint(Graphics window) {
        window.clearRect(0,0,600,600);
        current_state.paint(window);
    }

}




