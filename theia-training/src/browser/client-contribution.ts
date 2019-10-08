import { injectable } from "inversify";
import { BaseLanguageClientContribution, DocumentSelector } from '@theia/languages/lib/browser';

@injectable()
export class ClientContribution extends BaseLanguageClientContribution {

    readonly id = 'languageServerExample';
    readonly name = 'Language Server Example';

    // Register the server for plain text documents
    protected get documentSelector(): DocumentSelector | undefined {
        return [{ scheme: 'file', language: 'plaintext' }];
    }

    // Notify the server about file changes to '.clientrc files contained in the workspace
    protected get globPatterns(): string[] {
        return ['**/.clientrc'];
    }

    protected get configurationSection(): string[] {
        return [this.id];
    }

}