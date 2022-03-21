import {customElement, html, LitElement} from 'lit-element';

@customElement('sms-show-grid-view')
export class SmsShowGridView extends LitElement {

    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`
            <vaadin-board style="max-width: 100%; margin-right: auto; margin-left: auto">
                <vaadin-row>
                    <div id="row-header"></div>
                </vaadin-row>
                <vaadin-row>
                    <div id="row-body"></div>
                    <vaadin-grid id="smsGrid" style="min-height: 500px"></vaadin-grid>
                </vaadin-row>
                <vaadin-row>
                    <div id="row-footer"></div>
                </vaadin-row>
            </vaadin-board>
        `;
    }
}