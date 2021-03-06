/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.openmos.msb.services.rest;

import eu.openmos.model.ExecutionTable;
import eu.openmos.model.ExecutionTableRow;
import eu.openmos.model.ExecutionTable_DA;
import eu.openmos.model.SubSystem;
import eu.openmos.msb.datastructures.DACManager;
import eu.openmos.msb.datastructures.DeviceAdapter;
import eu.openmos.msb.datastructures.DeviceAdapterOPC;
import eu.openmos.msb.datastructures.MSBConstants;
import eu.openmos.msb.datastructures.MSBVar;
import eu.openmos.msb.services.rest.data.ExecutionTableRowHelper;
import eu.openmos.msb.utilities.Functions;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

/**
 *
 * @author Antonio Gatto <antonio.gatto@we-plus.eu>
 * @author Valerio Gentile <valerio.gentile@we-plus.eu>
 */
@Path("/api/v1/executiontables")
public class ExecutionTableController
{

  private final Logger logger = Logger.getLogger(ExecutionTableController.class.getName());

  /**
   * Returns the full execution table given its unique identifier. Fills the
   * execution table view page (slide 8 of 34).
   *
   * @param executionTableId
   * @return detail of execution table
   *
   * @param uniqueId the unique id of the execution table
   * @return executiontable object, or null if not existing
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/{executionTableId}")
  public ExecutionTable getDetail(@PathParam("executionTableId") String executionTableId)
  {
    logger.debug("execution table getDetail - executionTableId = " + executionTableId);
    DACManager DACinstance = DACManager.getInstance();
    List<String> deviceAdaptersID = DACinstance.getDeviceAdapters_AML_IDs();
    for (String da_id : deviceAdaptersID)
    {
      DeviceAdapter deviceAdapterbyName = DACinstance.getDeviceAdapterbyAML_ID(da_id);
      if (deviceAdapterbyName.getExecutionTable().getUniqueId().equals(executionTableId))
      {
        return deviceAdapterbyName.getExecutionTable();
      }
    }
    return null;
  }

  /**
   * Updates the whole execution table. Matches with the execution table update
   * pages (slide 9 to 12 of 34).
   *
   * return updated execution table
   *
   * @param subSystemId the execution table to update
   * @param executionTable
   * @return executiontable updated object, or null if not existing
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/{subSystemId}")
  public ExecutionTable update(@PathParam("subSystemId") String subSystemId,
          ExecutionTable executionTable)
  {
    logger.debug("execution table update - Update ExecutionTable from SubSystem: " + subSystemId);
    logger.debug("execution table update - data received by the msb: " + executionTable);

    //if (MSBVar.getSystemStage().equals(MSBConstants.STAGE_RAMP_UP))
    //{
    SubSystem subSystem = getSubSystemById(subSystemId);
    if (subSystem != null)
    {
      DeviceAdapter da = DACManager.getInstance().getDeviceAdapterbyAML_ID(subSystemId);
      String et_da_string = Functions.ClassToString(ExecutionTable_DA.createExecutionTable_DA(executionTable));

      DeviceAdapterOPC client = (DeviceAdapterOPC) da;
      OpcUaClient opcua_client = client.getClient().getClientObject();
      NodeId object_id = Functions.convertStringToNodeId(da.getSubSystem().getUpdateExectutionTableObjectID());
      NodeId method_id = Functions.convertStringToNodeId(da.getSubSystem().getUpdateExectutionTableMethodID());

      boolean ret = client.getClient().InvokeUpdate(opcua_client, object_id, method_id, et_da_string, false);
      logger.debug("EXECUTION TABLE UPDATE RESULT: " + ret);
      //TODO send it to DA
      return executionTable;
    } else
    {
      return null;
    }
    /*} else
    {
      logger.debug("The system is not at Ramp Up Stage!");
      return null;
    }
     */
  }

  /**
   * Insert the given row into the execution table. Matches with the execution
   * table update pages (slide 9 to 12 of 34).
   *
   * return updated execution table
   *
   * @param subSystemId unique identifier of the execution table to update
   * @param rowToInsert the execution table row to insert in which position in
   * which execution table
   * @return executiontable updated object, or null if not existing
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/{subSystemId}/newRow")
  public ExecutionTable insertRow(@PathParam("subSystemId") String subSystemId,
          ExecutionTableRowHelper rowToInsert)
  {
    logger.debug("execution table insert - Insert new row in ExecutionTable from SubSystem: " + subSystemId);
    logger.debug("execution table insert - data received by the msb: " + rowToInsert);

    SubSystem subSystem = getSubSystemById(subSystemId);
    if (subSystem != null)
    {
      DeviceAdapter da = DACManager.getInstance().getDeviceAdapterbyAML_ID(subSystemId);
      ExecutionTable et = da.getSubSystem().getExecutionTable();
      if (et.getRows() != null && et.getRows().isEmpty()
              && rowToInsert.getRowPosition() > 0)
      {
        //logger.debug("execution table insert - corretting new row position, setting to 0");
        rowToInsert.setRowPosition(0);
      }
      //logger.debug("execution table insert - new row position: " + rowToInsert.getRowPosition());

      et.getRows().add(rowToInsert.getRowPosition(), rowToInsert.getRow());
      String et_da_string = Functions.ClassToString(ExecutionTable_DA.createExecutionTable_DA(et));

      DeviceAdapterOPC client = (DeviceAdapterOPC) da;
      OpcUaClient opcua_client = client.getClient().getClientObject();
      NodeId object_id = Functions.convertStringToNodeId(da.getSubSystem().getUpdateExectutionTableObjectID());
      NodeId method_id = Functions.convertStringToNodeId(da.getSubSystem().getUpdateExectutionTableMethodID());

      boolean ret = client.getClient().InvokeUpdate(opcua_client, object_id, method_id, et_da_string, false);
      logger.debug("EXECUTION TABLE NEW ROW RESULT: " + ret);
      //TODO send the whole table to DA -> Lboro valentine's day discussions
    }
    logger.debug(subSystem != null
            ? "execution table insert - new row insert successfully"
            : "execution table insert - can not find subSystem with Id: " + subSystemId
    );
    return subSystem != null ? subSystem.getExecutionTable() : null;
  }

  /**
   * Returns list of rows of the given execution table.
   *
   * @param executionTableId
   * @return list of execution table rows
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/{executionTableId}/rows")
  public List<ExecutionTableRow> getRows(@PathParam("executionTableId") String executionTableId)
  {
    logger.debug("execution table getRows - executionTableId = " + executionTableId);

    DACManager DACinstance = DACManager.getInstance();
    List<String> deviceAdaptersID = DACinstance.getDeviceAdapters_AML_IDs();
    for (String da_id : deviceAdaptersID)
    {
      DeviceAdapter deviceAdapterbyName = DACinstance.getDeviceAdapterbyAML_ID(da_id);
      if (deviceAdapterbyName.getExecutionTable().getUniqueId().equals(executionTableId))
      {
        return deviceAdapterbyName.getExecutionTable().getRows();
      }
    }

    return null;
  }

  /**
   * Returns selected row of the given execution table.
   *
   * @param executionTableId
   * @param executionTableRowId
   * @return one execution table row
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/{executionTableId}/rows/{executionTableRowId}")
  public ExecutionTableRow getRow(
          @PathParam("executionTableId") String executionTableId,
          @PathParam("executionTableRowId") String executionTableRowId)
  {
    logger.debug("execution table getRow - executionTableId = " + executionTableId);
    logger.debug("execution table getRow - executionTableRowId = " + executionTableRowId);

    DACManager DACinstance = DACManager.getInstance();
    List<String> deviceAdaptersID = DACinstance.getDeviceAdapters_AML_IDs();
    for (String da_id : deviceAdaptersID)
    {
      DeviceAdapter deviceAdapterbyName = DACinstance.getDeviceAdapterbyAML_ID(da_id);
      if (deviceAdapterbyName.getExecutionTable().getUniqueId().equals(executionTableId))
      {
        List<ExecutionTableRow> rows = deviceAdapterbyName.getExecutionTable().getRows();
        for (ExecutionTableRow row : rows)
        {
          if (row.getUniqueId().equals(executionTableRowId))
          {
            return row;
          }
        }
      }
    }

    /* List<ExecutionTableRow> rows = ExecutionTableTest.getTestObject(executionTableId, ThreadLocalRandom.current().nextInt(1, 10 + 1)).getRows();
        for (ExecutionTableRow row : rows)
            if (row.getUniqueId().equalsIgnoreCase(executionTableRowId))
                    return row;*/
    return null;
  }

  /**
   * Deletes selected row of the given execution table.
   *
   * return updated execution table
   *
   * param uniqueId unique id of the execution table
   *
   * @param subSystemId
   * @param executionTableRowId unique id of the execution table row to be
   * deleted
   * @return updated executiontable, or null if not existing
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/{subSystemId}/rows/{executionTableRowId}")
  public ExecutionTable deleteRow(@PathParam("subSystemId") String subSystemId,
          @PathParam("executionTableRowId") String executionTableRowId)
  {

    logger.debug("execution table delete row - delete row with Id: "
            + executionTableRowId + " , from executionTable of subSystem: " + subSystemId);
    SubSystem subSystem = getSubSystemById(subSystemId);
    if (subSystem != null)
    {
      List<ExecutionTableRow> toRemove = new ArrayList<>();
      for (ExecutionTableRow row : subSystem.getExecutionTable().getRows())
      {
        if (row.getUniqueId().equalsIgnoreCase(executionTableRowId))
        {
          toRemove.add(row);
        }
      }
      subSystem.getExecutionTable().getRows().removeAll(toRemove);

      //TODO send the whole table to DA -> Lboro valentine's day discussions
    }
    logger.debug(subSystem != null
            ? "execution table delete row - Row successfully deleted from execution table"
            : "execution table delete row - can not find subSystem: " + subSystemId);
    return subSystem != null ? subSystem.getExecutionTable() : null;
  }

  /**
   * Updates selected row of the given execution table.
   *
   * @param executionTableId
   * @param rowToUpdate
   * @param executionTableRowId
   * @return updated execution table
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/{executionTableId}/rows/{executionTableRowId}")
  public ExecutionTable updateRow( //not used
          @PathParam("executionTableId") String executionTableId,
          @PathParam("executionTableRowId") String executionTableRowId,
          ExecutionTableRow rowToUpdate)
  {
    logger.debug("execution table updateRow - executionTableId = " + executionTableId);
    logger.debug("execution table updateRow - executionTableRowId = " + executionTableRowId);
    logger.debug("execution table updateRow - rowToUpdate = " + rowToUpdate);

    DACManager DACinstance = DACManager.getInstance();
    List<String> deviceAdaptersID = DACinstance.getDeviceAdapters_AML_IDs();
    for (String da_id : deviceAdaptersID)
    {
      DeviceAdapter deviceAdapterbyName = DACinstance.getDeviceAdapterbyAML_ID(da_id);
      if (deviceAdapterbyName.getExecutionTable().getUniqueId().equals(executionTableId))
      {
        List<ExecutionTableRow> rows = deviceAdapterbyName.getExecutionTable().getRows();
        for (ExecutionTableRow row : rows)
        {
          if (row.getUniqueId().equals(executionTableRowId))
          {
            row.setNextRecipeId(rowToUpdate.getNextRecipeId());
            row.setNextRecipeIdPath(rowToUpdate.getNextRecipeIdPath());
            row.setPossibleRecipeChoices(rowToUpdate.getPossibleRecipeChoices());
            row.setProductId(rowToUpdate.getProductId());
            row.setRecipeId(rowToUpdate.getRecipeId());
            row.setRegistered(rowToUpdate.getRegistered());
            row.setUniqueId(rowToUpdate.getUniqueId());

            //TODO send the whole table to DA -> Lboro valentine's day discussions
            return deviceAdapterbyName.getExecutionTable();
          }
        }
      }
    }

    return null;
  }

  private SubSystem getSubSystemById(String subSystemId)
  {
    for (SubSystem ss : (new SubSystemController()).getList())
    {
      if (ss.getUniqueId().equalsIgnoreCase(subSystemId))
      {
        return ss;
      }
    }
    return null;
  }
}
