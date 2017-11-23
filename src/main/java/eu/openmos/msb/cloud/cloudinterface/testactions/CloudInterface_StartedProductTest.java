package eu.openmos.msb.cloud.cloudinterface.testactions;

import eu.openmos.agentcloud.utilities.ServiceCallStatus;
import eu.openmos.agentcloud.ws.systemconfigurator.wsimport.SystemConfigurator;
import eu.openmos.agentcloud.ws.systemconfigurator.wsimport.SystemConfigurator_Service;
import eu.openmos.agentcloud.config.ConfigurationLoader;
import eu.openmos.model.FinishedProductInfo;
import eu.openmos.model.ProductInstance;
// import eu.openmos.agentcloud.data.recipe.Skill;
import eu.openmos.model.utilities.SerializationConstants;
import eu.openmos.model.testdata.ModuleTest;
import eu.openmos.model.testdata.OrderInstanceTest;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.ws.BindingProvider;
import org.apache.log4j.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
* WP4 Cloud Platform Re-work related code.
* 
* @author Pedro Lima Monteiro <pedro.monteiro@uninova.pt>
* @author Valerio Gentile <valerio.gentile@we-plus.eu>
*/
public class CloudInterface_StartedProductTest {
    private static final Logger logger = Logger.getLogger(CloudInterface_StartedProductTest.class.getName());
    
    public static void main(String[] args) {
        logger.info("Started Product Test main start");
        
        SystemConfigurator_Service systemConfiguratorService = new SystemConfigurator_Service();
	SystemConfigurator systemConfigurator = systemConfiguratorService.getSystemConfiguratorImplPort();
        String CLOUDINTERFACE_WS_VALUE = ConfigurationLoader.getMandatoryProperty("openmos.agent.cloud.cloudinterface.ws.endpoint");
        logger.info("Agent Cloud Cloudinterface address = [" + CLOUDINTERFACE_WS_VALUE + "]");           
        BindingProvider bindingProvider = (BindingProvider) systemConfigurator;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, CLOUDINTERFACE_WS_VALUE);

        ProductInstance productInstance = OrderInstanceTest.getTestObject().getProductInstances().get(0);
        productInstance.setUniqueId("pd1400uniqueid_20171031163108");
        
        ServiceCallStatus skillStatus = systemConfigurator.startedProduct(productInstance);
          
        logger.info("New Started Product Test main end");
    }
}