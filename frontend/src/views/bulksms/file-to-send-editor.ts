import {customElement, html, LitElement} from 'lit-element';

// @ts-ignore
@customElement('file-to-send-editor')
export class FileToSendEditor extends LitElement {
    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`

            <style include="shared-styles">
                :host {
                    display: flex;
                    flex-direction: column;
                    flex: auto;
                }

                .meta-row {
                    display: flex;
                    justify-content: space-between;
                    padding-bottom: var(--lumo-space-s);
                }

                .dim {
                    color: var(--lumo-secondary-text-color);
                    text-align: right;
                    white-space: nowrap;
                    line-height: 2.5em;
                }

                .status {
                    width: 10em;
                }
            </style>
            <div class="scrollable flex1" id="main">
                <h2 id="title">New order</h2>

                <div class="meta-row" id="metaContainer">
                    <vaadin-combo-box class="status" id="status"></vaadin-combo-box>
                    <span class="dim">Order #<span id="orderNumber"></span></span>
                </div>

                <vaadin-form-layout id="form1">
                    <vaadin-form-layout id="form2">
                        <vaadin-date-time-picker label="Due" id="dueDate"></vaadin-date-time-picker>
                        <vaadin-checkbox id="sendNow">Enviar Ya</vaadin-checkbox>
                    </vaadin-form-layout>

                    <vaadin-form-layout id="form3" colspan="3">
                        <vaadin-text-field id="orderName" label="Nombre de Agenda" colspan="2">
                            <iron-icon slot="prefix" icon="vaadin:user"></iron-icon>
                        </vaadin-text-field>

                        <multi-select-combo-box id="systemId" label="Credencial">
                            <iron-icon slot="prefix" icon="vaadin:phone"></iron-icon>
                        </multi-select-combo-box>

                        <vaadin-text-field id="orderDescription" label="Descripcion" colspan="3"></vaadin-text-field>
                        <vaadin-text-area id="message" label="Escriba su mensaje"colspan="3"></vaadin-text-area>

                        <vaadin-form-item colspan="3">
                            <p id="charCounter"></p>
                            <span id="warningSpan"></span>
                            <p id="messageBuilded"></p>
                        </vaadin-form-item>
                    </vaadin-form-layout>
                </vaadin-form-layout>
            </div>

            <buttons-bar id="footer" no-scroll\\$="[[noScroll]]">
                <vaadin-button slot="left" id="cancel">Cancel</vaadin-button>
                <div slot="info" class="total">Total [[totalPrice]]</div>
                <vaadin-button slot="right" id="review" theme="primary">
                    Review order
                    <iron-icon icon="vaadin:arrow-right" slot="suffix"></iron-icon>
                </vaadin-button>
            </buttons-bar>

        `;
    }
}