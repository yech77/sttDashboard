import {customElement, html, LitElement} from 'lit-element';

@customElement('sms-show-grid-view-v2')
export class SmsShowGridViewV2 extends LitElement {

    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`
            <vaadin-board style="max-width: 100%; margin-right: auto; margin-left: auto">
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
                    <vaadin-horizontal-layout>
                        <vaadin-horizontal-layout>
                            <vaadin-button id="grid-btn-download" class="action-btn" theme="secondary">Download
                        </vaadin-horizontal-layout>
                        <vaadin-horizontal-layout>
                            <vaadin-button id="grid-btn-close" class="action-btn" theme="primary success">Cerrarr
                            </vaadin-button>
                        </vaadin-horizontal-layout>
                    </vaadin-horizontal-layout>
                </vaadin-board-row>
            </vaadin-board>
        `;
    }
}