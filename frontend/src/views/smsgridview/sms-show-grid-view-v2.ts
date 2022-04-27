import {customElement, html, LitElement} from 'lit-element';

@customElement('sms-show-grid-view-v2')
export class SmsShowGridViewV2 extends LitElement {

    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`
            <vaadin-board>
                <vaadin-board-row>
                    <vaadin-vertical-layout>
                        <h3 id="grid-title"></h3>
                        <h5 id="grid-subtitle"></h5>
                    </vaadin-vertical-layout>
                </vaadin-board-row>
                <vaadin-board-row>
                    <vaadin-grid id="smsGrid" style="min-height: 500px"></vaadin-grid>
                </vaadin-board-row>
                <vaadin-board-row>
                    <buttons-bar id="footer" class="vaadin-board-cell">
                        <vaadin-button slot="right" id="grid-btn-close" theme="primary">Cerrar</vaadin-button>
                        <vaadin-button slot="left" id="grid-btn-download" theme="secondary">Preparar descarga
                        </vaadin-button>
                    </buttons-bar>
                </vaadin-board-row>
            </vaadin-board>
        `;
    }
}