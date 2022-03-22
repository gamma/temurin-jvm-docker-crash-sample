import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

class ImageReadTest {

    public static void main(String[] args) throws Exception {
        URL[] urls = Arrays.asList(
            new File("asm.jar").toURI().toURL(),
            new File("imageio-openjpeg.jar").toURI().toURL(),
            new File("jffi-native.jar").toURI().toURL(),
            new File("jffi.jar").toURI().toURL(),
            new File("jnr-ffi.jar").toURI().toURL(),
            new File("jpeg2000.jar").toURI().toURL(),
            new File("slf4j-api.jar").toURI().toURL()
        ).toArray( new URL[]{} );
        
        // path to native libs. FIXME: Update hte path to your system
        System.setProperty( "jnr.ffi.library.path", new File("openjpeg/linux").getAbsolutePath() );
        System.out.println( "Check if the follwing path corresponds to your system: " + System.getProperty( "jnr.ffi.library.path" ) );

        // load the openjpeg classes using classloader to resemble our custom setup
        ClassLoader loader = new URLClassLoader(urls);
        Class<?> OpenJp2ImageReaderSpi = loader.loadClass( "de.digitalcollections.openjpeg.imageio.OpenJp2ImageReaderSpi" );

        // register in ImageIO
        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider( OpenJp2ImageReaderSpi.getDeclaredConstructor().newInstance() );

        // test if it is working
        try (InputStream testLogo = ImageReadTest.class.getResourceAsStream( "verification.jp2" ) ){
            BufferedImage testImage = ImageIO.read( testLogo );
            if( testImage == null ) {
                throw new IOException( "OpenJpeg library not initalized correctly, Jpeg2000 images will not be displayed." );
            }
            System.out.println( "OpenJpeg library initalized correctly, Jpeg2000 images will be displayed." );
        } catch (Throwable e) {
		    e.printStackTrace();
	    }
    }
}