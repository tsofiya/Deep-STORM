/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

import net.imagej.Data;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImageJ;
import net.imagej.axis.Axes;
import net.imagej.axis.Axis;
import net.imagej.axis.AxisType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;

/**
 * A command that generates a diagonal gradient image of user-given size.
 * <p>
 * For an even simpler command, see {@link NetworkDemo} in this same
 * package!
 * </p>
 */
@Plugin(type = Command.class, headless = true,
	menuPath = "File>New>Gradient Image")
public class GradientImage implements Command {

	@Parameter
	private DatasetService datasetService;

	@Parameter(min = "1")
	private int width = 512;

	@Parameter(min = "1")
	private int height = 512;

	@Parameter(type = ItemIO.OUTPUT)
	private Dataset dataset;

	@Override
	public void run() {
		// Generate a byte array containing the diagonal gradient.
		final byte[] data = new byte[width * height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				final int index = y * width + x;
				data[index] = (byte) (x + y);
			}
		}

		final byte[] data1 = new byte[width * height];
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				final int index = y * height + x;
				data1[index] = (byte)150;
			}
		}


		// Create an empty dataset.

		final long[] dims = { width, height};
		final AxisType[] axes = { Axes.X, Axes.Y};
		dataset=( datasetService.create(new UnsignedByteType(), dims, "Image1", axes));
		//dataset.add( datasetService.create(new UnsignedByteType(), dims, "Image2", axes));


		byte [] bigData= new byte[2*data.length];
		System.arraycopy(data,0,bigData,0 ,data.length);
		System.arraycopy(data1,0,bigData,data.length,data1.length);
		// Populate the dataset with the gradient data.
		dataset.setPlane(0, bigData);

		// NB: Because the dataset is declared as an "OUTPUT" above,
		// ImageJ automatically takes care of displaying it afterwards!
	}

	/** Tests our command. */
	public static void main(final String... args) throws Exception {
		// Launch ImageJ as usual.
		final ImageJ ij = new ImageJ();
		ij.launch(args);

		// Launch the "Gradient Image" command right away.
		ij.command().run(GradientImage.class, true);
	}

}
