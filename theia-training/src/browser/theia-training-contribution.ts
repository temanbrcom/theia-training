import { injectable, inject } from "inversify";
import { CommandContribution, CommandRegistry, MenuContribution, MenuModelRegistry, MessageService } from "@theia/core/lib/common";
import { CommonMenus } from "@theia/core/lib/browser";

export const TheiaTrainingCommand = {
    id: 'TheiaTraining.command',
    label: "Shows a message"
};

@injectable()
export class TheiaTrainingCommandContribution implements CommandContribution {

    constructor(
        @inject(MessageService) private readonly messageService: MessageService,
    ) { }

    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(TheiaTrainingCommand, {
            execute: () => this.messageService.info('Greetingsasdfdf')
        });
    }
}

@injectable()
export class TheiaTrainingMenuContribution implements MenuContribution {

    registerMenus(menus: MenuModelRegistry): void {
        menus.registerMenuAction(CommonMenus.EDIT_FIND, {
            commandId: TheiaTrainingCommand.id,
            label: 'Say Hello'
        });
    }
}