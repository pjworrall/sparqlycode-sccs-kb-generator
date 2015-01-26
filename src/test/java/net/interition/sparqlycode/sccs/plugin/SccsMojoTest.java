package net.interition.sparqlycode.sccs.plugin;

import static org.junit.Assert.*;

import java.io.File;

import net.interition.sparqlycode.sccs.SccsMojo;

import org.junit.Test;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class SccsMojoTest
    extends AbstractMojoTestCase
{
    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        // required
        super.setUp();

    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        // required
        super.tearDown();
    }

    /**
     * @throws Exception if any
     */
    @Test
    public void testSomething()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/test-pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        SccsMojo sccsMojo = (SccsMojo) lookupMojo( "screpo-maven-plugin", pom );
        assertNotNull( sccsMojo );
        sccsMojo.execute();
        
        // pass it through while I figure out how to use this plugin test framework
        assertTrue(true);

    }
}
