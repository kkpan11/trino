/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import io.airlift.json.JsonCodec;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.facebook.presto.spi.ColumnType.LONG;
import static com.facebook.presto.spi.ColumnType.STRING;
import static io.airlift.json.JsonCodec.listJsonCodec;
import static io.airlift.json.JsonCodec.mapJsonCodec;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TestExampleClient
{
    private static final JsonCodec<Map<String, List<ExampleTable>>> CATALOG_CODEC = mapJsonCodec(String.class, listJsonCodec(ExampleTable.class));

    @Test
    public void testMetadata()
            throws Exception
    {
        URL metadataUrl = Resources.getResource(TestExampleClient.class, "/example-data/example-metadata.json");
        assertNotNull(metadataUrl, "metadataUrl is null");
        URI metadata = metadataUrl.toURI();
        ExampleClient client = new ExampleClient(new ExampleConfig().setMetadata(metadata), CATALOG_CODEC);
        assertEquals(client.getSchemaNames(), ImmutableSet.of("example", "tpch"));
        assertEquals(client.getTableNames("example"), ImmutableSet.of("numbers"));
        assertEquals(client.getTableNames("tpch"), ImmutableSet.of("orders", "lineitem"));

        ExampleTable table = client.getTable("example", "numbers");
        assertNotNull(table, "table is null");
        assertEquals(table.getName(), "numbers");
        assertEquals(table.getColumns(), ImmutableList.of(new ExampleColumn("text", STRING), new ExampleColumn("value", LONG)));
        assertEquals(table.getSources(), ImmutableList.of(metadata.resolve("numbers-1.csv"), metadata.resolve("numbers-2.csv")));
    }
}
