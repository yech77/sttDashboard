var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { customElement, html, LitElement } from 'lit-element';
let FileToSendFrontView = class FileToSendFrontView extends LitElement {
    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }
    render() {
        return html `
            <div>
                <vaadin-date-picker label="Due" id="dueDate"></vaadin-date-picker>
            </div>
        `;
    }
};
FileToSendFrontView = __decorate([
    customElement('search-sms-bar')
], FileToSendFrontView);
export { FileToSendFrontView };
//# sourceMappingURL=search-sms-bar.js.map