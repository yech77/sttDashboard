import {customElement, html, LitElement} from 'lit-element';

@customElement('sms-view')
export class SmsView extends LitElement {

    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`
            <style include="shared-styles">
                :host {
                    position: relative;
                    z-index: 2;
                    display: flex;
                    flex-direction: column;
                    overflow: hidden;
                    padding: 0 var(--lumo-space-s);
                    background-image: linear-gradient(var(--lumo-shade-20pct), var(--lumo-shade-20pct));
                    background-color: var(--lumo-base-color);
                    box-shadow: 0 0 16px 2px var(--lumo-shade-20pct);
                    order: 1;
                    width: 100%;
                    height: 48px;
                }

                @media (min-width: 700px) {
                    .row {
                        width: 100%;
                        max-width: 964px;
                        margin: 0 auto;
                    }
                    .field {
                         padding-right: var(--lumo-space-m);
                     }
                }
            </style>
            <div class="row" id="firstline"></div>
            <div class="row" id="secondline"></div>
            <div>
                <vaadin-board style="max-width: 964px; margin-right: auto; margin-left: auto">
                    <vaadi-row>
                        <vaadin-grid id ="smsGrid"></vaadin-grid>
                    </vaadi-row>
                    <vaadin-row>
                        <div id="footer"></div>
                    </vaadin-row>
                </vaadin-board>
            </div>
        `;
    }
}