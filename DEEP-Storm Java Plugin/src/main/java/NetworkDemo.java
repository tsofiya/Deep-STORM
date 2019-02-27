/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

import net.imagej.Dataset;
import net.imagej.ImageJ;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.ejml.simple.SimpleMatrix;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.springframework.core.io.ClassPathResource;


import java.util.ArrayList;

/**
 * A very simple plugin.
 * <p>
 * The annotation {@code @Plugin} lets ImageJ know that this is a plugin. There
 * are a vast number of possible plugins; {@code Command} plugins are the most
 * common one: they take inputs and produce outputs.
 * </p>
 * <p>
 * A {@link Command} is most useful when it is bound to a menu item; that is
 * what the {@code menuPath} parameter of the {@code @Plugin} annotation does.
 * </p>
 * <p>
 * Each input to the command is specified as a field with the {@code @Parameter}
 * annotation. Each output is specified the same way, but with a
 * {@code @Parameter(type = ItemIO.OUTPUT)} annotation.
 * </p>
 * 
 * @author Johannes Schindelin
 * @author Curtis Rueden
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Deep-STORM>Demo")
public class NetworkDemo implements Command {


	@Parameter(type = ItemIO.INPUT)
	private Dataset input;


	//When an image is loaded, run your network
	@Override
	public void run() {
		ArrayList<SimpleMatrix> images;
		try {
			images= Utilities.ReadStackFromTiff(this.input.getSource());
		}
		catch (Exception e){
			System.out.println(e);
			return;
		}

		MultiLayerNetwork model;
		try {
			//opening the saved model.
			String simpleMlp = new ClassPathResource("simple_mlp.h5").getFile().getPath();
			model = KerasModelImport.importKerasSequentialModelAndWeights(simpleMlp);
		}catch (Exception e){
			System.out.println(e);
			return;
		}

		double[] meanStd;


	}

	//main method for testing purpose.
	public static void main(final String... args) {
		// Launch ImageJ as usual.
		final ImageJ ij = new ImageJ();
		ij.launch(args);
	}

}
