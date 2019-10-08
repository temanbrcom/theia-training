/**
 * Generated using theia-extension-generator
 */

import { TheiaTrainingCommandContribution, TheiaTrainingMenuContribution } from './theia-training-contribution';
import {
    CommandContribution,
    MenuContribution
} from "@theia/core/lib/common";

import { ContainerModule } from "inversify";

export default new ContainerModule(bind => {
    // add your contribution bindings here
    
    bind(CommandContribution).to(TheiaTrainingCommandContribution);
    bind(MenuContribution).to(TheiaTrainingMenuContribution);
    
});