package eu.openmos.msb.dds;

/*
 * OpenSplice DDS
 *
 * This software and documentation are Copyright 2006 to 2013 PrismTech Limited and its licensees. All rights reserved.
 * See file:
 *
 * $OSPL_HOME/LICENSE
 *
 * for full copyright notice and license terms.
 *
 */
import DDS.*;
import MSB2ADAPTER.GeneralMethodMessage;
import MSB2ADAPTER.GeneralMethodMessageDataReader;
import MSB2ADAPTER.GeneralMethodMessageDataReaderImpl;
import MSB2ADAPTER.GeneralMethodMessageSeqHolder;
import MSB2ADAPTER.StringMessage;
import MSB2ADAPTER.StringMessageDataReader;
import MSB2ADAPTER.StringMessageDataReaderImpl;
import MSB2ADAPTER.StringMessageSeqHolder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListenerDataListener implements DDS.DataReaderListener
{

  @Override
  public void on_data_available(DataReader arg0)
  {
    System.out.println("AVAILABLE");
    int status;

    if (arg0 instanceof GeneralMethodMessageDataReaderImpl)
    {
      //OPCDeviceHelper deviceItf = new OPCDeviceHelper();

      System.out.println("GeneralMethodMessageDataReaderImpl");

      GeneralMethodMessageSeqHolder msgList = new GeneralMethodMessageSeqHolder();
      SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

      status = ((GeneralMethodMessageDataReader) arg0).take(
              msgList,
              infoSeq,
              LENGTH_UNLIMITED.value,
              NOT_READ_SAMPLE_STATE.value,
              NEW_VIEW_STATE.value,
              ANY_INSTANCE_STATE.value);

      DDSErrorHandler.checkStatus(status, "MsgDataReader.read");

      GeneralMethodMessage[] data = msgList.value;
      boolean hasValidData = false;

      if (data.length > 0)
      {

        System.out.println("=== [ListenerDataListener.on_data_available] - msgList.length : " + data.length);
        int i = 0;
        do
        {
          if (infoSeq.value[i].valid_data)
          {
            hasValidData = true;
            System.out.println("    --- message received ---");
            System.out.println("    Device    : " + data[i].device);
            System.out.println("    Function  : " + data[i].function);
            System.out.println("    args      : " + data[i].args);
            System.out.println("    Feedbak   : " + data[i].feedback);
            System.out.println("    ------------------------");

            try
            {
              String s = data[i].device + ":" + data[i].args;
              //deviceItf.allCases(data[i].function, s);
            } catch (Exception ex)
            {
              Logger.getLogger(ListenerDataListener.class.getName()).log(Level.SEVERE, null, ex);
            }

          }
        } while (++i < data.length);

        status = ((GeneralMethodMessageDataReader) arg0).return_loan(msgList, infoSeq);
        DDSErrorHandler.checkStatus(status, "MsgDataReader.return_loan");

      }
    } else if (arg0 instanceof StringMessageDataReaderImpl)
    {

      if (((StringMessageDataReaderImpl) arg0).get_topicdescription().get_name().compareTo("registo_fabio") == 0)
      {

        StringMessageSeqHolder msgList = new StringMessageSeqHolder();
        SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

        status = ((StringMessageDataReader) arg0).take(
                msgList,
                infoSeq,
                LENGTH_UNLIMITED.value,
                NOT_READ_SAMPLE_STATE.value,
                NEW_VIEW_STATE.value,
                ANY_INSTANCE_STATE.value);

        DDSErrorHandler.checkStatus(status, "MsgDataReader.read");

        StringMessage[] data = msgList.value;
        boolean hasValidData = false;

        if (data.length > 0)
        {

          System.out.println("=== [ListenerDataListener.on_data_available] - msgList.length : " + data.length);
          int i = 0;
          do
          {
            if (infoSeq.value[i].valid_data)
            {
              hasValidData = true;
              System.out.println("    --- message received ---");
              System.out.println("    Device    : " + data[i].device);
              System.out.println("    args      : " + data[i].args);
              System.out.println("    ------------------------");

              // TODO - af-silva add support again to DDS after changes to the MSB core
            }
          } while (++i < data.length);
        }

      }

    }
  }

  @Override
  public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1)
  {

    System.out.println("=== [ListenerDataListener.on_liveliness_changed] : triggered");

  }

  @Override
  public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1)
  {

    System.out.println("=== [ListenerDataListener.on_requested_deadline_missed] : triggered");
    // System.out.println("=== [ListenerDataListener.on_requested_deadline_missed] : stopping");

  }

  @Override
  public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1)
  {

    System.out.println("=== [ListenerDataListener.on_requested_incompatible_qos] : triggered");

  }

  @Override
  public void on_sample_lost(DataReader arg0, SampleLostStatus arg1)
  {

    System.out.println("=== [ListenerDataListener.on_sample_lost] : triggered");

  }

  @Override
  public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1)
  {

    System.out.println("=== [ListenerDataListener.on_sample_rejected] : triggered");

  }

  @Override
  public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1)
  {

    System.out.println("=== [ListenerDataListener.on_subscription_matched] : triggered");

  }

}
