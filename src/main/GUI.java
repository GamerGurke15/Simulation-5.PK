package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

public class GUI extends JFrame{
	/**
	 * frames per second of the simulation<br>
	 * (effects the speed of objects moving
	 */
	final float fps = 60;

	/**
	 * main method<br>
	 * -->runs the simulation
	 * @param args -
	 */
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				GUI frame = new GUI(600, 300);
				frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

	Container p;
	JLabel lbl1;
	JSpinner EnergyLoss;
	JButton BasketStart;
	JButton FederStart;
	JButton reset;
	JPanel SimuPnl;
	Ball BallLeft;
	Ball BallRight;
	Feder FederLeft;
	Feder FederRight;
	Thread BasketThread;
	Thread FederThread;

	GUI(int width, int height){
		// init GUI
		super("Simulation 5. PK Ball Sound Leonard und Niklas");
		setSize(new Dimension(width, height));
		setBackground(Color.white);
		SpringLayout l = new SpringLayout();
		p = this.getContentPane();
		setLayout(l);

		// init comps
		lbl1 = new JLabel(
				"<html><center>Verbleibende Energie nach einem Peak einstellen<br>"
						+ "und gewünschtes Modell starten.</center></html>");

		EnergyLoss = new JSpinner(new SpinnerNumberModel(80, 0, 100, 1));
		EnergyLoss.setPreferredSize(new Dimension(50, 25));
		EnergyLoss.setEditor(new JSpinner.NumberEditor(EnergyLoss));

		reset = new JButton("Reset");
		reset.setBackground(Color.white);
		/**
		 * ActionListener of reset Button
		 */
		reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				reset();
				BasketThread.interrupt();
				FederThread.interrupt();

				EnergyLoss.setEnabled(true);
				BasketStart.setEnabled(true);
				FederStart.setEnabled(true);
			}
		});

		BasketStart = new JButton("Baskteballmodell");
		BasketStart.setBackground(Color.white);
		/**
		 * ActionListener of BasketModellButton
		 */
		BasketStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				reset();
				FederLeft.setVisible(true);
				FederRight.setVisible(true);
				EnergyLoss.setEnabled(false);
				BasketStart.setEnabled(false);
				FederStart.setEnabled(false);
				BasketThread.start();
			}
		});
		BasketThread = new Thread(new BasketModell());

		FederStart = new JButton("Federmodell");
		FederStart.setBackground(Color.white);
		/**
		 * ActionListener of FederModellButton
		 */
		FederStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				reset();
				FederLeft.setVisible(true);
				FederRight.setVisible(true);
				EnergyLoss.setEnabled(false);
				BasketStart.setEnabled(false);
				FederStart.setEnabled(false);
				FederThread.start();
			}
		});
		FederThread = new Thread(new FederModell());

		// creating and filling the simulation panel
		SimuPnl = new JPanel();
		SimuPnl.setPreferredSize(new Dimension(500, 100));
		SimuPnl.setBorder(BorderFactory.createLineBorder(Color.black));
		SimuPnl.setLayout(null);

		reset();

		SimuPnl.add(FederLeft);
		SimuPnl.add(FederRight);
		SimuPnl.add(BallRight);
		SimuPnl.add(BallLeft);

		// putting constraints of GUI
		l.putConstraint(SpringLayout.HORIZONTAL_CENTER, lbl1, 0,
				SpringLayout.HORIZONTAL_CENTER, p);
		l.putConstraint(SpringLayout.NORTH, lbl1, 20, SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.NORTH, EnergyLoss, 5, SpringLayout.SOUTH,
				lbl1);
		l.putConstraint(SpringLayout.EAST, EnergyLoss, -5,
				SpringLayout.HORIZONTAL_CENTER, p);
		l.putConstraint(SpringLayout.NORTH, reset, 5, SpringLayout.SOUTH, lbl1);
		l.putConstraint(SpringLayout.WEST, reset, 5,
				SpringLayout.HORIZONTAL_CENTER, p);
		l.putConstraint(SpringLayout.NORTH, BasketStart, 10,
				SpringLayout.SOUTH, EnergyLoss);
		l.putConstraint(SpringLayout.EAST, BasketStart, -5,
				SpringLayout.HORIZONTAL_CENTER, p);
		l.putConstraint(SpringLayout.NORTH, FederStart, 10, SpringLayout.SOUTH,
				EnergyLoss);
		l.putConstraint(SpringLayout.WEST, FederStart, 5,
				SpringLayout.HORIZONTAL_CENTER, p);
		l.putConstraint(SpringLayout.NORTH, SimuPnl, 10, SpringLayout.SOUTH,
				FederStart);
		l.putConstraint(SpringLayout.HORIZONTAL_CENTER, SimuPnl, 0,
				SpringLayout.HORIZONTAL_CENTER, p);

		// adding components to the JFrame
		add(lbl1);
		add(EnergyLoss);
		add(reset);
		add(BasketStart);
		add(FederStart);
		add(SimuPnl);
	}

	/**
	 * reseting the simulation panel<br>
	 * -->(re)creating and placing components on SimulationPanel
	 */
	private void reset(){
		FederLeft = new Feder(50, 10, 0);
		Dimension size = FederLeft.getPreferredSize();
		FederLeft.setBounds(0, 45, size.width, size.height);

		FederRight = new Feder(50, 10, 1);
		size = FederRight.getPreferredSize();
		FederRight.setBounds(450, 45, size.width, size.height);

		BallLeft = new Ball(66, 66, 2);
		size = BallLeft.getPreferredSize();
		BallLeft.setBounds(50 - 1, 17, size.width, size.height);

		BallRight = new Ball(66, 66, 3);
		size = BallRight.getPreferredSize();
		BallRight.setBounds(384, 17, size.width, size.height);
	}

	/**
	 * moves the component to x
	 * @param comp - the <code>JComponent</code> that should be moved
	 * @param x - the x coordinate of the new components position
	 * @param Ball - true if the component is instance of class
	 *        <code>Ball</code>
	 * @param id - the id of <code>comp</code>
	 */
	private synchronized void moveComponent(JComponent comp, Rectangle r, int id){
		moveComponent(comp, r.x, r.y, r.width, r.height, id);
	}

	/**
	 * moves the component to x
	 * @param comp - the <code>JComponent</code> that should be moved
	 * @param x - the x coordinate of the new components position
	 * @param Ball - true if the component is instance of class
	 *        <code>Ball</code>
	 * @param id - the id of <code>comp</code>
	 */
	private synchronized void moveComponent(JComponent comp, int x, int y,
			int w, int h, int id){
		int index = getIndex(comp, id);
		SimuPnl.remove(index);
		if (id >= 2){
			comp = new Ball(w, h, id);
		} else{
			comp = new Feder(w, h, id);
		}
		comp.setLocation(x, y);
		SimuPnl.add(comp);
		SimuPnl.revalidate();
		SimuPnl.repaint();
		comp.revalidate();
	}

	/**
	 * returns the index of the component on the SimulationPanel
	 * @param comp -
	 *        <code>JComponent<code> of which the index should be returned needs to be on the SimulationPanel
	 * @return the id of the given <code>comp</code>
	 */
	private int getIndex(JComponent comp, int id){
		if (id >= 2)
			for (int i = 0; i < SimuPnl.getComponentCount(); i++){
				if (SimuPnl.getComponent(i).getClass() == comp.getClass()
						&& ((Ball) SimuPnl.getComponent(i)).getID() == ((Ball) comp)
								.getID()){
					return i;
				}
			}
		else
			for (int i = 0; i < SimuPnl.getComponentCount(); i++){
				if (SimuPnl.getComponent(i).getClass() == comp.getClass()
						&& ((Feder) SimuPnl.getComponent(i)).getID() == ((Feder) comp)
								.getID()){
					return i;
				}
			}
		return -1;
	}

	//
	//
	//
	// Modelle (eigentlicher Code)
	//
	//
	//

	class BasketModell implements Runnable{
		public void run(){
			long time;
			while (true){
				time = System.currentTimeMillis();

				// the action todo

				// timing
				try{
					Thread.sleep((long) ((1000f / fps) - (System
							.currentTimeMillis() - time)));
				} catch (InterruptedException e){
					break;
				}
			}
		}
	}

	Rectangle posBallLeft = new Rectangle();
	Rectangle posFederLeft = new Rectangle();
	Rectangle posBallRight = new Rectangle();
	Rectangle posFederRight = new Rectangle();

	double v = 0;
	boolean Aufprall = false;
	int Mitte = 250;
	int counter = 0;

	class FederModell implements Runnable{
		public void run(){
			long time;
			int EL = (int) EnergyLoss.getValue();
			posBallLeft.x = 0;
			posBallLeft.y = BallLeft.getY();
			posBallLeft.height = BallLeft.getHeight();
			posBallLeft.width = BallLeft.getWidth();

			posFederLeft.x = FederLeft.getX();
			posFederLeft.y = FederLeft.getY();
			posFederLeft.height = FederLeft.getHeight();
			posFederLeft.width = posBallLeft.x - posFederLeft.x;
			
			posBallRight.x = 450-66;
			posBallRight.y = BallRight.getY();
			posBallRight.height = BallRight.getHeight();
			posBallRight.width = BallRight.getWidth();

			posFederLeft.x = posBallRight.x+66;
			posFederRight.y = FederRight.getY();
			posFederRight.height = FederRight.getHeight();
			posFederRight.width = SimuPnl.getWidth() -  posFederRight.x;

			while (true){
				time = System.currentTimeMillis();
				moveComponent(BallLeft, posBallLeft, BallLeft.getID());
				moveComponent(FederLeft, posFederLeft, FederLeft.getID());
				moveComponent(BallRight, posBallRight, BallRight.getID());
				moveComponent(FederRight, posFederRight, FederRight.getID());

				posBallLeft.x += Math.round(v);
				posFederLeft.width += Math.round(v);
				
				posBallRight.x -= Math.round(v);
				posFederRight.x -= Math.round(v);
				posFederRight.width += Math.round(v);
				// calculating new v
				if (!Aufprall){
					v += Math.pow((Mitte - (posBallLeft.x + 66))
							/ (double) (Mitte - 50), 2) * 0.5;
				} else{
					if (counter == Math.round(EL * 0.25))
						break;
					v *= -EL/100;
					Aufprall = false;
				}
				if (posBallLeft.x + 66 >= Mitte)
					Aufprall = true;

				// timing
				try{
					Thread.sleep((long) ((1000f / fps) - (System
							.currentTimeMillis() - time)));
					counter++;
				} catch (Exception e){
					break;
				}
				System.out.println(posBallLeft.x + " " + v);
			}
		}
	}

	//
	//
	//
	// Komponenten
	//
	//
	//

	class Ball extends JComponent{
		int id;

		public Ball(int width, int height, int id){
			super();
			setSize(new Dimension(width, height));
			this.id = id;
		}

		public int getID(){
			return id;
		}

		public void paint(Graphics g){
			g.setColor(Color.black);
			g.fillOval(0, 0, this.getWidth(), this.getHeight());
		}
	}

	class Feder extends JComponent{
		int id;

		public Feder(int width, int height, int id){
			super();
			setPreferredSize(new Dimension(width, height));
			this.id = id;
		}

		public int getID(){
			return id;
		}

		public void paint(Graphics g){
			g.setColor(Color.black);
			g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
		}
	}
}
