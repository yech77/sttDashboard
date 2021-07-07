import {customElement, html, LitElement} from 'lit-element';

@customElement('search-sms-bar')
export class FileToSendFrontView extends LitElement {

    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`
            <div>
                <vaadin-date-picker label="Due" id="dueDate"></vaadin-date-picker>
            </div>
        `;
    }
}