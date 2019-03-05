import com.twelvemonkeys.imageio.metadata.exif.TIFF;
import ij.ImagePlus;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import org.ejml.simple.SimpleMatrix;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
public class returningTiff {


    public static void main(String[]args){
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(new File("D:\\repositories\\deepStromTsofiya\\Deep-STORM\\demo 1 - Simulated Microtubules\\testStack_SimulatedMicrotubules.tif"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageReader reader = ImageIO.getImageReaders(iis).next();
        reader.setInput(iis);

        try {
            ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
            for (int i = 0; i < reader.getNumImages(true); i++) {
                frames.add(reader.read(i));
            }

            Utilities.SaveImagesTiff(new ArrayList<BufferedImage>(frames), " OutPutTiff.tiff");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
