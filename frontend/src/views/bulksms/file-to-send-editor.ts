import {customElement, html, LitElement} from 'lit-element';
import {FormLayoutResponsiveStep} from "@vaadin/vaadin-form-layout";

// @ts-ignore
@customElement('file-to-send-editor')
export class FileToSendEditor extends LitElement {
    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    private responsiveSteps: FormLayoutResponsiveStep[] = [
        {columns: 1, labelsPosition: 'top'},
        {minWidth: '600px', columns: 4, labelsPosition: 'top'}
    ];
    private form2: FormLayoutResponsiveStep[] = [
        {columns: 1, labelsPosition: 'top'},
        {minWidth: '360px', columns: 2, labelsPosition: 'top'}
    ];
    private form3: FormLayoutResponsiveStep[] = [
        {columns: 1, labelsPosition: 'top'},
        {minWidth: '500px', columns: 3, labelsPosition: 'top'}
    ];

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
                <h2 id="title">Nueva Programacion</h2>

                <div class="meta-row" id="metaContainer">
                    <span class="dim">Order #<span id="orderNumber"></span></span>
                </div>

                <vaadin-form-layout id="form1" .responsiveSteps="${this.responsiveSteps}">
                    <vaadin-form-layout id="form2" .responsiveSteps="${this.form2}">
                        <vaadin-date-time-picker label="Due" id="dueDate"></vaadin-date-time-picker>
                        <vaadin-checkbox id="sendNow">Enviar Ya</vaadin-checkbox>
                    </vaadin-form-layout>

                    <vaadin-form-layout id="form3" colspan="3" .responsiveSteps="${this.form3}">
                        <vaadin-combo-box id="status" label="Agenda" class="status" colspan="2"></vaadin-combo-box>
                        <iron-icon slot="prefix" icon="vaadin:phone"></iron-icon>
                        </vaadin-combo-box>
                        <vaadin-combo-box id="systemId" label="Credencial"></vaadin-combo-box>
                        <vaadin-text-field id="orderName" label="Nombre de Programacion" colspan="2"
                                           clear-button-visible></vaadin-text-field>

                        <!--                        <multiselect-combo-box id="systemId" label="Credencial">-->
                        <!--                            <iron-icon slot="prefix" icon="vaadin:phone"></iron-icon>-->
                        <!--                        </multiselect-combo-box>-->

                        <vaadin-text-field id="orderDescription" label="Descripcion" colspan="2"
                                           clear-button-visible></vaadin-text-field>
                        <vaadin-text-area id="message" label="Escriba su mensaje" colspan="2" required
                                          clear-button-visible></vaadin-text-area>

                        <vaadin-form-item colspan="2">
                            <p id="charCounter"></p>
                            <span id="warningSpan"></span>
                            <p id="messageBuilded"></p>
                        </vaadin-form-item>
                    </vaadin-form-layout>
                </vaadin-form-layout>
            </div>

            <buttons-bar id="footer" no-scroll\\$="[[noScroll]]">
                <vaadin-button slot="left" id="cancel">Cancel</vaadin-button>
                <vaadin-button slot="right" id="review" theme="primary">
                    Confirmar Programacion
                    <iron-icon icon="vaadin:arrow-right" slot="suffix"></iron-icon>
                </vaadin-button>
            </buttons-bar>

        `;
    }

    static get is() {
        return 'file-to-send-editor';
    }

    static get properties() {
        return {
            status: {
                type: String,
                observer: '_onStatusChange'
            }
        };
    }

    // ready() {
    //     // super.ready();
    //
    //     // Not using attributes since Designer does not suppor single-quote attributes
    //     this.form1.responsiveSteps = [
    //     ];
    //     this.$.form2.responsiveSteps = [
    //         {columns: 1, labelsPosition: 'top'},
    //         {minWidth: '360px', columns: 2, labelsPosition: 'top'}
    //     ];
    //     this.$.form3.responsiveSteps = [
    //         {columns: 1, labelsPosition: 'top'},
    //         {minWidth: '500px', columns: 3, labelsPosition: 'top'}
    //     ];
    // }
}