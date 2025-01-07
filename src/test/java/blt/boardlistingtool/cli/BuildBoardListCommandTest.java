package blt.boardlistingtool.cli;

import blt.boardlistingtool.config.CliAppConfiguration;
import blt.boardlistingtool.localfs.BoardFileProviderAdaptor;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.autoconfigure.ShellTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ShellTest
@Import({CliAppConfiguration.class, BoardFileProviderAdaptor.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BuildBoardListCommandTest {

    private final static String OUTPUT_FILE = "src/test/resources/boards_" + UUID.randomUUID() + ".json";

    @Autowired
    ShellTestClient client;

    @AfterEach
    void setUp() {
        //Deleting output file if exists
        Paths.get(OUTPUT_FILE).toFile().delete();
    }

    @Test
    void testFailsIfNoParametersPassed() {
        var session = client.nonInterative("build").run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            ShellAssertions.assertThat(session.screen())
                    .containsText("Missing mandatory option '--inputDir'")
                    .containsText("Missing mandatory option '--outputFile'");
        });
    }

    @Test
    void testFailsIfOutputFileNotJson() {
        var session = client.nonInterative("build", "--inputDir", "folderName", "--outputFile", "test.txt").run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            ShellAssertions.assertThat(session.screen())
                    .containsText("Option '--outputFile' must specify a JSON file");
        });
    }

    @Test
    void buildsBoardList() throws IOException {
        var session = client.nonInterative("build",
                        "--inputDir", "src/test/resources/example-boards",
                        "--outputFile", OUTPUT_FILE)
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            assertTrue(session.isComplete());
            assertTrue(Files.exists(Paths.get(OUTPUT_FILE)));
        });

        ObjectMapper mapper = new ObjectMapper();
        try (var parser = mapper.createParser(Paths.get(OUTPUT_FILE).toFile())) {
            TreeNode resultTree = parser.readValueAsTree();
            assertEquals(3, getInt(resultTree, "/_metadata/total_boards"));
            assertEquals(2, getInt(resultTree, "/_metadata/total_vendors"));

            assertBoard(resultTree, "/boards/0/", "B7-400X", "Boards R Us", "Cortex-M7", true);
            assertBoard(resultTree, "/boards/1/", "D4-200S", "Boards R Us", "Cortex-M4", false);
            assertBoard(resultTree, "/boards/2/", "Low_Power", "Tech Corp.", "Cortex-M0+", false);
        }
    }

    @Test
    void buildsBoardListExcludingDuplicates() throws IOException {
        var session = client.nonInterative("build",
                        "--inputDir", "src/test/resources/duplicated-boards",
                        "--outputFile", OUTPUT_FILE)
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            assertTrue(session.isComplete());
            assertTrue(Files.exists(Paths.get(OUTPUT_FILE)));
        });

        ObjectMapper mapper = new ObjectMapper();
        try (var parser = mapper.createParser(Paths.get(OUTPUT_FILE).toFile())) {
            TreeNode resultTree = parser.readValueAsTree();
            assertEquals(3, getInt(resultTree, "/_metadata/total_boards"));
            assertEquals(2, getInt(resultTree, "/_metadata/total_vendors"));

            assertBoard(resultTree, "/boards/0/", "B7-400X", "Boards R Us", "Cortex-M7", true);
            assertBoard(resultTree, "/boards/1/", "D4-200S", "Boards R Us", "Cortex-M4", false);
            assertBoard(resultTree, "/boards/2/", "Low_Power", "Tech Corp.", "Cortex-M0+", false);
        }
    }

    @Test
    void buildsBoardListWithDuplicateNameAndVendorButNotProperties() throws IOException {
        var session = client.nonInterative("build",
                        "--inputDir", "src/test/resources/same-vendor-and-name-boards",
                        "--outputFile", OUTPUT_FILE)
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            assertTrue(session.isComplete());
            assertTrue(Files.exists(Paths.get(OUTPUT_FILE)));
        });

        ObjectMapper mapper = new ObjectMapper();
        try (var parser = mapper.createParser(Paths.get(OUTPUT_FILE).toFile())) {
            TreeNode resultTree = parser.readValueAsTree();
            assertEquals(5, getInt(resultTree, "/_metadata/total_boards"));
            assertEquals(1, getInt(resultTree, "/_metadata/total_vendors"));

            assertBoard(resultTree, "/boards/0/", "B7-400X", "Boards R Us", "Cortex-M7", false);
            assertBoard(resultTree, "/boards/1/", "B7-400X", "Boards R Us", "Cortex-M7", true);
            assertBoard(resultTree, "/boards/2/", "B7-400X", "Boards R Us", "Cortex-M7+", true);
            assertBoard(resultTree, "/boards/3/", "B7-400X", "Boards R Us", "Cortex-M7-", true);
            assertBoard(resultTree, "/boards/4/", "B7-400X", "Boards R Us", "Cortex-M7X", true);
        }
    }

    @Test
    void failsIfSourceFileContainsUnexpectedFields() throws IOException {
        var session = client.nonInterative("build",
                        "--inputDir", "src/test/resources/bad-schema",
                        "--outputFile", OUTPUT_FILE)
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            assertTrue(session.isComplete());
            ShellAssertions.assertThat(session.screen())
                    .containsText("UnrecognizedPropertyException")
                    .containsText("price");
        });
    }


    private static void assertBoard(TreeNode resultTree, String pathPrefix, String name,
                                    String vendor, String core, boolean hasWifi) {
        assertEquals(name, getStr(resultTree, pathPrefix + "name"));
        assertEquals(vendor, getStr(resultTree, pathPrefix + "vendor"));
        assertEquals(core, getStr(resultTree, pathPrefix + "core"));
        assertEquals(hasWifi, getBool(resultTree, pathPrefix + "has_wifi"));
    }

    private static int getInt(TreeNode tree, String path) {
        return ((IntNode) tree.at(path)).intValue();
    }

    private static String getStr(TreeNode tree, String path) {
        return ((TextNode) tree.at(path)).asText();
    }

    private static boolean getBool(TreeNode tree, String path) {
        return ((BooleanNode) tree.at(path)).asBoolean();
    }
}