package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

	final int BallLeftID = 0;
	final int BallRightID = 1;
	final int FederLeftID = 2;
	final int FederRightID = 3;
	final int Mitte = 250;

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
	SimuPanel SimuPnl;
	Thread BasketThread;
	Thread FederThread;
	Rectangle[] Coordinates;
	int counter;

	GUI(int width, int height){
		// init GUI
		super("Simulation 5. PK Ball Sound Leonard und Niklas");
		setSize(new Dimension(width, height));
		setBackground(Color.white);
		SpringLayout l = new SpringLayout();
		p = this.getContentPane();
		setLayout(l);

		// init Coordinates
		Coordinates = new Rectangle[]{new Rectangle(49, 17, 66, 66), // BallLeft
				new Rectangle(384, 17, 66, 66), // BallRight
				new Rectangle(0, 45, 50, 10), // FederLeft
				new Rectangle(450, 45, 50, 10)};// FederRight

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
				EnergyLoss.setEnabled(false);
				BasketStart.setEnabled(false);
				FederStart.setEnabled(false);
				BasketThread = new Thread(new BasketModell());
				BasketThread.start();
			}
		});

		FederStart = new JButton("Federmodell");
		FederStart.setBackground(Color.white);
		/**
		 * ActionListener of FederModellButton
		 */
		FederStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				reset();
				EnergyLoss.setEnabled(false);
				BasketStart.setEnabled(false);
				FederStart.setEnabled(false);
				FederThread = new Thread(new FederModell());
				FederThread.start();
			}
		});
		// creating and filling the simulation panel
		SimuPnl = new SimuPanel();
		SimuPnl.setPreferredSize(new Dimension(500, 100));
		SimuPnl.setBorder(BorderFactory.createLineBorder(Color.black));
		SimuPnl.setLayout(null);
		reset();

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
		if (FederThread != null)
			FederThread.interrupt();
		if (BasketThread != null)
			BasketThread.interrupt();
		
		counter = 0;

		Coordinates[BallLeftID] = new Rectangle(50 - 1, 17, 66, 66);

		Coordinates[BallRightID] = new Rectangle(384, 17, 66, 66);

		Coordinates[FederLeftID] = new Rectangle(0, 45, 50, 10);

		Coordinates[FederRightID] = new Rectangle(450, 45, 50, 10);

		SimuPnl.repaint();
	}

	/**
	 * moves the component to x
	 * @param comp - the <code>JComponent</code> that should be moved
	 * @param x - the x coordinate of the new components position
	 * @param Ball - true if the component is instance of class
	 *        <code>Ball</code>
	 * @param id - the id of <code>comp</code>
	 */
	private synchronized void moveComponent(int id, Rectangle r){
		Coordinates[id] = r;
		SimuPnl.repaint();
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
			while (!Thread.interrupted()){
				time = System.currentTimeMillis();

				// the action todo
				moveComponent(BallLeftID, Coordinates[BallLeftID]);

				Coordinates[BallLeftID].x++;
				// timing
				try{
					Thread.sleep((long) ((1000f / fps) - (System
							.currentTimeMillis() - time)));
				} catch (InterruptedException e){
					break;
				}
			}
			FederThread.interrupt();
		}
	}

	class FederModell implements Runnable{
		public void run(){
			long time;
			int EL = (int) EnergyLoss.getValue();

			double v = 0;// velocity
			boolean Aufprall = false;// true if they collide

			while (!Thread.interrupted()){
				time = System.currentTimeMillis();
				moveComponent(BallLeftID, Coordinates[BallLeftID]);
				moveComponent(FederLeftID, Coordinates[FederLeftID]);
				moveComponent(BallRightID, Coordinates[BallRightID]);
				moveComponent(FederRightID, Coordinates[FederRightID]);

				Coordinates[BallLeftID].x = (int) (Coordinates[BallLeftID].x + Math.round(v));
				Coordinates[FederLeftID].width = Coordinates[BallLeftID].x;

				Coordinates[BallRightID].x = (int) (Coordinates[BallRightID].x - Math.round(v));
				Coordinates[FederRightID].x = Coordinates[BallRightID].x + 66;
				Coordinates[FederRightID].width = SimuPnl.getWidth() - Coordinates[FederRightID].x;

				// calculating new v
				if (!Aufprall){
					v = v + Math.pow((Mitte - (Coordinates[BallLeftID].x))
							/ (double) (Mitte - 50), 2) * 0.5;
				} else{
					v = -v * EL / 100;
					Coordinates[BallLeftID].x = Mitte - 70;
					Coordinates[BallRightID].x = Mitte - 5;
					counter++;
					if (counter > Math.round(EL * 0.2)) break;
				}
				Aufprall = (Coordinates[BallLeftID].x + 66 >= Mitte)
						? true
						: false;

				// timing
				try{
					Thread.sleep((long) ((1000f / fps) - (System
							.currentTimeMillis() - time)));
				} catch (Exception e){
					break;
				}
				System.out.println(counter + " " + v);
			}
			System.err.println("Simulation ended");
			FederThread.interrupt();
		}
	}

	//
	//
	//
	// Komponenten
	//
	//
	//

	class SimuPanel extends JPanel{
		public void paint(Graphics g){
			super.paint(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.black);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			// BallLeft
			g2d.fillOval(Coordinates[BallLeftID].x, Coordinates[BallLeftID].y,
					Coordinates[BallLeftID].width,
					Coordinates[BallLeftID].height);

			// BallRight
			g2d.fillOval(Coordinates[BallRightID].x,
					Coordinates[BallRightID].y, Coordinates[BallRightID].width,
					Coordinates[BallRightID].height);

			// FederLeft (fillOvel to drawRect if rectangle)
			g2d.fillOval(Coordinates[FederLeftID].x,
					Coordinates[FederLeftID].y, Coordinates[FederLeftID].width,
					Coordinates[FederLeftID].height);

			// FederRight (fillOvel to drawRect if rectangle)
			g2d.fillOval(Coordinates[FederRightID].x,
					Coordinates[FederRightID].y,
					Coordinates[FederRightID].width,
					Coordinates[FederRightID].height);

		}
	}
}
