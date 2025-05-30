// Galgo.java
package arsw.threads;

/**
 * Un galgo que puede correr en un carril
 *
 * @author rlopez
 */
public class Galgo extends Thread {
	private int paso;
	private Carril carril;
	private RegistroLlegada regl;
	private Canodromo can;

	public Galgo(Canodromo can, Carril carril, String name, RegistroLlegada reg) {
		super(name);
		this.can = can;
		this.carril = carril;
		this.regl = reg;
		this.paso = 0;
	}

	public void corra() throws InterruptedException {
		while (paso < carril.size()) {
			// 1) Si estamos en pausa, esperamos aquí
			can.waitIfPaused();

			// 2) Simulamos tiempo de carrera
			Thread.sleep(100);

			// 3) Avanzamos un paso y actualizamos UI
			carril.setPasoOn(paso++);
			carril.displayPasos(paso);

			// 4) Si llegó a la meta, registramos posición
			if (paso == carril.size()) {
				carril.finish();
				int ubicacion = regl.getUltimaPosicionAlcanzada();
				regl.setUltimaPosicionAlcanzada(ubicacion + 1);
				System.out.println("El galgo " + this.getName() +
						" llegó en la posición " + (ubicacion + 1));
				if (ubicacion == 0) {
					regl.setGanador(this.getName());
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			corra();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
