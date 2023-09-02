import {html, LitElement} from 'lit-element';

class SmsShowGridView extends LitElement {

    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`
            <vaadin-board>
                <vaadin-row>
                    <div id="row-header" class="vaadin-board-cell"></div>
                </vaadin-row>
                <vaadin-row>
                    <div id="row-body" class="vaadin-board-cell"></div>
                    <vaadin-grid id="smsGrid" style="min-height: 500px"></vaadin-grid>
                </vaadin-row>
                <vaadin-row>
                    <div id="row-footer" class="vaadin-board-cell"></div>
                </vaadin-row>
            </vaadin-board>
        `;
    }
}

customElements.define('sms-show-grid-view', SmsShowGridView)