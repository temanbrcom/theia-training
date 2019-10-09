package lsp4j.server;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

public class MyLanguageServer implements LanguageServer, TextDocumentService, WorkspaceService, LanguageClientAware {

    private LanguageClient client;

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        CompletionOptions completionOptions = new CompletionOptions();
        completionOptions.setResolveProvider(true);
        capabilities.setCompletionProvider(completionOptions);
        // TODO allow code actions and execute command requests
        return CompletableFuture.completedFuture(new InitializeResult(capabilities));
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.completedFuture(new Object());
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return this;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this;
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {

    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {

    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        this.validateTextDocument(params.getTextDocument().getUri(), params.getTextDocument().getText());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        this.validateTextDocument(params.getTextDocument().getUri(), params.getContentChanges().get(0).getText());
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {

    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {

    }

    static Pattern PATTERN = Pattern.compile("\\b[A-Z]{2,}\\b");

    protected void validateTextDocument(String uri, String content) {
        PublishDiagnosticsParams diagnostics = new PublishDiagnosticsParams();
        diagnostics.setUri(uri);
        String[] lines = content.split("\\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Matcher matcher = MyLanguageServer.PATTERN.matcher(line);
            while (matcher.find()) {
                Range range = new Range(new Position(i, matcher.start()), new Position(i, matcher.end()));
                Diagnostic diagnostic = new Diagnostic(range, matcher.group() + " is all uppercase.",
                        DiagnosticSeverity.Warning, "ex");
                diagnostics.getDiagnostics().add(diagnostic);
            }
        }
        this.client.publishDiagnostics(diagnostics);
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
        CompletionItem tsItem = new CompletionItem("TypeScript");
        tsItem.setKind(CompletionItemKind.Text);
        tsItem.setData(1);
        CompletionItem jsItem = new CompletionItem("JavaScript");
        jsItem.setKind(CompletionItemKind.Text);
        jsItem.setData(2);
        return CompletableFuture.completedFuture(Either.forLeft(Arrays.asList(tsItem, jsItem)));
    }

    @Override
    public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
        if (Integer.valueOf(1).equals(unresolved.getData())) {
            unresolved.setDetail("TypeScript details");
            unresolved.setDocumentation("TypeScript documentation");
        } else if (Integer.valueOf(2).equals(unresolved.getData())) {
            unresolved.setDetail("JavaScript details");
            unresolved.setDocumentation("JavaScript documentation");
        }
        return CompletableFuture.completedFuture(unresolved);
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
        // TODO provide to lowercase quick fixes for in uppercase warnings
        return TextDocumentService.super.codeAction(params);
    }

    @Override
    public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
        // TODO apply the quick fix
        return WorkspaceService.super.executeCommand(params);
    }

}