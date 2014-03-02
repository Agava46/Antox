package im.tox.antox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

/**
 * Class for loading and saving the data file to be used by JTox(byte[] data,
 * ...)
 * 
 * @author Mark Winter (astonex)
 */
public class ToxDataFile {

	/**
	 * Other clients use the file name data so we shall as well
	 */
	private String fileName = "data";

	public ToxDataFile() {
	}

	/**
	 * Method to check if the data file exists before attempting to use it
	 * @return
	 */
	public boolean doesFileExist() {
		File myFile = new File("/sdcard/" + fileName);
		return myFile.exists();
	}

	/**
	 * Method for deleting the tox data file
	 */
	public void deleteFile() {
		File file = new File("/sdcard/" + fileName);
		file.delete();
	}

    /**
     * Check if external storage is available to read and write
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if external storage is available to read
     * @return
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

	/**
	 * Method for loading data from a saved file and return it. Requires the
	 * context of the activity or service calling it.
	 *
	 * @return
	 */
	public byte[] loadFile() {
		FileInputStream fin = null;
		final File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath(), fileName);
		byte[] data = null;
		try {
			fin = new FileInputStream(file);
			data = new byte[(int) file.length()];
			fin.read(data);

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}
		}
		return data;
	}

	/**
	 * Method for saving tox data. Requires the data to be saved and the context
	 * of the activity or service calling it
	 * 
	 * @param dataToBeSaved
	 */
	public void saveFile(byte[] dataToBeSaved) {
		File myFile = new File("/sdcard/" + fileName);
		try {
			myFile.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			FileOutputStream output = new FileOutputStream(myFile);
			output.write(dataToBeSaved, 0, dataToBeSaved.length);
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}