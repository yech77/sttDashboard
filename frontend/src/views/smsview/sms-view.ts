import {customElement, html, LitElement} from 'lit-element';

@customElement('sms-view')
export class SmsView extends LitElement {

    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`
            <vaadin-board>
                <div id="header"></div>
            </vaadin-board>
        `;
    }
}