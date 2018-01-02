package zokoo.wsd_chat.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import zokoo.wsd_chat.Entities.Container;
import zokoo.wsd_chat.Entities.Message;

/**
 * Created by Piotyr-Szef on 17.12.2017.
 */

public class DataController {

    static private DataController dataController = null;

    static public DataController getController()
    {
        if(dataController==null)
        {
            dataController = new DataController();
        }
        return dataController;
    }

    public byte[] getData()
    {

    }

    public void setData (Container container)throws IOException
    {

        String key = "1616161BBB117177"; // 128 bit key
        String initVector = "JPJPjpjpjp100100"; // 16 bytes IV
        //TODO find the way to convert user password to key and iv

        byte[] b;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(container);
        oos.flush();
        b = baos.toByteArray();

        byte[] encrypted = AEScipher.encrypt(key, initVector, b);

        //TODO saving to file
    }

    public void addMessage(Message message)
    {

    }

    public void deleteMessage(long id)
    {

    }

}
