// Canodromo.java
package arsw.threads;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Interfaz de usuario y modelo para un Canodromo
 *
 * @author rlopez
 */
public class Canodromo extends JFrame {
	private static final long serialVersionUID = 1L;

	private Carril[] carril;
	private JButton butStart = new JButton("Start");
	private JButton butStop = new JButton("Stop");
	private JButton butContinue = new JButton("Continue");

	// >>> bloqueo y bandera para pausa
	private final Object pauseLock = new Object();
	private volatile boolean paused = false;

	public Canodromo(int nCarriles, int longPista) {
		carril = new Carril[nCarriles];
		for (int i = 0; i < nCarriles; i++) {
			carril[i] = new Carril(longPista, "" + i);
		}

		JPanel cont = (JPanel) getContentPane();
		cont.setLayout(new BorderLayout());

		JPanel panPistas = new JPanel(new GridLayout(nCarriles, 1));
		for (int row = 0; row < nCarriles; row++) {
			JPanel panCarril = new JPanel(new BorderLayout());
			JPanel panTrack = new JPanel(new GridLayout(1, longPista));
			for (int col = 0; col < longPista; col++) {
				panTrack.add(carril[row].getPaso(col));
			}
			panCarril.add(panTrack, BorderLayout.CENTER);
			panCarril.add(carril[row].getLlegada(), BorderLayout.EAST);
			panPistas.add(panCarril);
		}
		panPistas.setBorder(new EmptyBorder(new Insets(5, 0, 5, 0)));
		cont.add(panPistas, BorderLayout.NORTH);

		JPanel butPanel = new JPanel(new FlowLayout());
		butPanel.add(butStart);
		butPanel.add(butStop);
		butPanel.add(butContinue);
		cont.add(butPanel, BorderLayout.SOUTH);

		this.setSize(longPista * 8, nCarriles * 20 + 400);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((dim.width - getWidth())/2, (dim.height - getHeight())/2);
		this.setTitle("Canodromo");
		this.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	// Métodos para pausa / reanudar
	public void pauseRace() {
		paused = true;
	}

	public void resumeRace() {
		synchronized (pauseLock) {
			paused = false;
			pauseLock.notifyAll();
		}
	}

	public void waitIfPaused() throws InterruptedException {
		synchronized (pauseLock) {
			while (paused) {
				pauseLock.wait();
			}
		}
	}

	// Accesores
	public Carril getCarril(int i) { return carril[i]; }
	public int getNumCarriles() { return carril.length; }

	public void setStartAction(ActionListener action)   { butStart.addActionListener(action); }
	public void setStopAction(ActionListener action)    { butStop.addActionListener(action); }
	public void setContinueAction(ActionListener action){ butContinue.addActionListener(action); }

	public void winnerDialog(String winner, int total) {
		JOptionPane.showMessageDialog(null,
				"El ganador fue: " + winner + " de un total de " + total);
	}

	public void restart() {
		for (Carril c : carril) {
			c.reStart();
		}
	}
}
