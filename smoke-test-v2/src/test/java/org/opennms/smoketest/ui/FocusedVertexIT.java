package org.opennms.smoketest.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennms.smoketest.ui.topology.TopologyIT;


public class FocusedVertexIT extends OpenNMSSeleniumIT {

    private TopologyIT.TopologyUIPage topologyUIPage;

    @Before
    public void setUp() {
        topologyUIPage = new TopologyIT.TopologyUIPage(this, "");
    }

    @Test
    public void testEqualsAndHashCode() {
        TopologyIT.FocusedVertex focusedVertex1 = new TopologyIT.FocusedVertex(topologyUIPage, "namespace1", "id");
        TopologyIT.FocusedVertex focusedVertex2 = new TopologyIT.FocusedVertex(topologyUIPage, "namespace1", "id");
        TopologyIT.FocusedVertex focusedVertex3 = new TopologyIT.FocusedVertex(topologyUIPage, "namespace2", "id");

        Assert.assertEquals(focusedVertex1, focusedVertex1);
        Assert.assertEquals(focusedVertex1, focusedVertex2);
        Assert.assertNotEquals(focusedVertex1, focusedVertex3);

        Assert.assertEquals(focusedVertex1.hashCode(), focusedVertex2.hashCode());
        Assert.assertNotEquals(focusedVertex1.hashCode(), focusedVertex3.hashCode());
    }
}