/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.openmos.msb.services.soap;

import eu.openmos.model.ProductInstance;
import eu.openmos.msb.database.interaction.DatabaseInteraction;
import eu.openmos.msb.datastructures.DACManager;
import eu.openmos.msb.datastructures.DeviceAdapter;
import eu.openmos.msb.datastructures.PECManager;
import eu.openmos.msb.datastructures.PerformanceMasurement;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;
import org.apache.log4j.Logger;

/**
 *
 * @author valerio.gentile
 */
@WebService(endpointInterface = "eu.openmos.msb.services.soap.EventConfirmation", serviceName = "EventConfirmation")
public class EventConfirmationImpl implements EventConfirmation
{

  private static final Logger logger = Logger.getLogger(EventConfirmationImpl.class.getName());

  @Override
  public boolean agentCreated(String agentId)
  {
    DeviceAdapter deviceAdapter = DACManager.getInstance().getDeviceAdapterbyAML_ID(agentId);
    if (deviceAdapter != null)
    {
      deviceAdapter.setHasAgent(true);
    } else
    {
      System.out.println("No DA found for the created agent ! " + agentId);
      System.out.println("Trying to check if it is a product agent... ");
      for (ProductInstance prodInst : PECManager.getInstance().getProductsToDo())
      {
        System.out.println("[agentCreated]prod instances Todo: " + prodInst.getUniqueId());
        if (agentId.equals(prodInst.getUniqueId()))
        {
          System.out.println("It is!");
          prodInst.setHasAgent(true);
        }
      }
    }
    PerformanceMasurement perfMeasure = PerformanceMasurement.getInstance();
    Long getStartTime = perfMeasure.getAgentCreationTimers().get(agentId);
    if (getStartTime != null)
    {
      Long diffTime = new Date().getTime() - getStartTime;
      perfMeasure.getAgentCreationTimers().put(agentId, diffTime);
    }

    return true;
  }

  @Override
  public boolean agentNotCreated(String agentId)
  {
    logger.debug(getClass().getName() + " - agentNotCreated - begin - tracing for agentId [" + agentId + "]");

    // some stuff...
    DeviceAdapter deviceAdapter = DACManager.getInstance().getDeviceAdapterbyAML_ID(agentId);
    if (deviceAdapter != null)
    {
      //deviceAdapter.setHasAgent(false);
    } else
    {
      System.out.println("No DA found for the created agent ! " + agentId);
    }

    logger.debug(getClass().getName() + " - agentNotCreated - end for agentId [" + agentId + "]");

    PerformanceMasurement perfMeasure = PerformanceMasurement.getInstance();
    Long getStartTime = perfMeasure.getAgentCreationTimers().get(agentId);
    if (getStartTime != null)
    {
      Long diffTime = new Date().getTime() - getStartTime;
      perfMeasure.getAgentCreationTimers().put(agentId, diffTime);
    }

    return true;
  }

  @Override
  public boolean agentRemoved(String agentId)
  {
    logger.debug(getClass().getName() + " - agentRemoved - begin - stopping websocket for agentId [" + agentId + "]");
    if (agentId == null || agentId.length() == 0)
    {
      logger.warn(getClass().getName() + " - agentRemoved - agentId [" + agentId + "] null or empty - returning");
      return false;
    }

    DeviceAdapter deviceAdapter = null;
    deviceAdapter = DACManager.getInstance().getDeviceAdapterbyName(DatabaseInteraction.getInstance().getDeviceAdapterNameByAmlID(agentId));
    if (deviceAdapter != null)
    {
      deviceAdapter.setHasAgent(false);
    } else
    {
      System.out.println("No DA found for the created agent ! " + agentId);
    }
    logger.debug(getClass().getName() + " - agentRemoved - end for agentId [" + agentId + "]");

    PerformanceMasurement perfMeasure = PerformanceMasurement.getInstance();
    Long getStartTime = perfMeasure.getAgentRemovalTimers().get(agentId);
    if (getStartTime != null)
    {
      Long diffTime = new Date().getTime() - getStartTime;
      perfMeasure.getAgentRemovalTimers().put(agentId, diffTime);
    }
    return true;
  }

  @Override
  public boolean agentNotRemoved(String agentId)
  {
    logger.debug(getClass().getName() + " - agentNotRemoved - begin - tracing for agentId [" + agentId + "]");

    // some stuff...
    logger.debug(getClass().getName() + " - agentNotRemoved - end for agentId [" + agentId + "]");
      PerformanceMasurement perfMeasure = PerformanceMasurement.getInstance();
      Long getStartTime = perfMeasure.getAgentRemovalTimers().get(agentId);
      if (getStartTime != null) {
          Long diffTime = new Date().getTime() - getStartTime;
          perfMeasure.getAgentRemovalTimers().put(agentId, diffTime);
      }
    return true;
  }

  @Override
  public boolean orderInstanceNotCreated(String orderId)
  {
    logger.debug(getClass().getName() + " - orderNotCreated - begin - for orderId [" + orderId + "]");

    // some stuff...
    logger.debug(getClass().getName() + " - orderNotCreated - end for orderId [" + orderId + "]");

    return true;
  }

  @Override
  public boolean orderInstanceCreated(String orderId, List<String> agentIds)
  {
    logger.debug(getClass().getName() + " - orderCreated - begin -  for orderId [" + orderId + "] and agentIds [" + agentIds + "]");

    // some stuff...
    for (String agentId : agentIds)
    {
//////      Vertx.vertx().deployVerticle(new WebSocketsSender(agentId));
//////
//////      // emulation!
//////      // add the created agents into the agents list
//////      // so that the msb emulator starts sending messages
//////      String agentsListFile = ConfigurationLoader.getMandatoryProperty("openmos.msb.agents.list.file");
//////      logger.debug("agentsListFile = [" + agentsListFile + "]");
//////      String agentsList = new String();
//////      try
//////      {
//////        agentsList = new String(Files.readAllBytes(Paths.get(agentsListFile)));
//////      } catch (IOException ex)
//////      {
//////        logger.error("cant read file " + agentsListFile + ": " + ex.getMessage());
//////      }
//////      if (agentsList.indexOf(agentId) == -1) // for sure
//////      {
//////        agentsList = agentsList.concat(agentId).concat("___");
//////        try
//////        {
//////          Files.write(Paths.get(agentsListFile), agentsList.getBytes());
//////        } catch (IOException ex)
//////        {
//////          logger.error("cant write file " + agentsListFile + ": " + ex.getMessage());
//////        }
//////      }

    }

    logger.debug(getClass().getName() + " - orderCreated - end for orderId [" + orderId + "] and agentIds [" + agentIds + "]");

    return true;
  }

  /**
   * When the cloud has started, notifies the MSB in order to receive workstations transports orders and so on.
   */
  @Override
  public void cloudActive()
  {
    logger.info("CLOUD notified it started and is working properly.");
    
    //create agents for the DAs
    List<String> daIDs = DACManager.getInstance().getDeviceAdapters_AML_IDs();
    
    for (String da_id : daIDs)
    {
      DeviceAdapter da = DACManager.getInstance().getDeviceAdapterbyAML_ID(da_id);
      if (!da.getSubSystem().getName().toUpperCase().contains("MSB"))
      {
        String daAgentCreation = DACManager.daAgentCreation(da);
        System.out.println("Agent creation for | " + da.getSubSystem().getName() + " | ended with: " + daAgentCreation);
        //da.initVertx();
      }
    }
  }
}
