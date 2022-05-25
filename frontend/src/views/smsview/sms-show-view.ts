import {customElement, html, LitElement} from 'lit-element';

@customElement('sms-show-view')
export class SmsShowView extends LitElement {

    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`
            <style include="shared-styles">
                :host {
                    width: 100%;
                    -webkit-overflow-scrolling: touch;
                    overflow: auto;
                }

                .vaadin-board-cell {
                    padding: var(--lumo-space-s);
                }

                *::-ms-backdrop,
                .vaadin-board-cell {
                    padding: 0;
                }

                .column-chart {
                    box-shadow: 0 2px 5px 0 rgba(23, 68, 128, 0.1);
                    border-radius: 4px;
                    height: calc(20vh - 64px) !important;
                    min-height: 200px;
                }

                .column-daily-chart {
                    box-shadow: 0 2px 5px 0 rgba(23, 68, 128, 0.1);
                    border-radius: 4px;
                    height: calc(20vh - 64px) !important;
                    min-height: 250px;
                }

                .pie-chart {
                    box-shadow: 0 2px 5px 0 rgba(23, 68, 128, 0.1);
                    border-radius: 4px;
                    height: calc(20vh - 64px) !important;
                    min-height: 250px;
                }

                #carrierDailyChart {
                    height: calc(30vh - 64px) !important;
                    min-height: 200px;
                }

                #monthlyProductSplit,
                #ordersGrid {
                    border-radius: 4px;
                    box-shadow: 0 2px 5px 0 rgba(23, 68, 128, 0.1);
                    height: calc(40vh - 64px) !important;
                    min-height: 355px;
                }

                vaadin-board-row.custom-board-row {
                    --vaadin-board-width-medium: 1440px;
                    --vaadin-board-width-small: 1024px;
                }

            </style>
            <vaadin-board>
                <!--                <vaadin-board-row class="vaadin-board-cell">-->
                <!--                    <vaadin-form-layout>-->
                <!--                        <vaadin-date-picker id="P1" colspan="2"></vaadin-date-picker>-->
                <!--                        <vaadin-date-picker id="P2"></vaadin-date-picker>-->
                <!--                        <vaadin-combo-box id="beanCheckboxGroup"></vaadin-combo-box>-->
                <!--                    </vaadin-form-layout>-->
                <!--                </vaadin-board-row>-->
                <vaadin-row>
                    <div id="firstline" class="vaadin-board-cell"></div>
                    <div id="secondline" class="vaadin-board-cell"></div>
                </vaadin-row>
                <buttons-bar id="footerr" class="vaadin-board-cell">
                    <vaadin-button slot="right" id="filterButton" theme="primary">Buscar</vaadin-button>
                </buttons-bar>
                <vaadin-row>
                    <vaadin-grid id="smsGrid" style="min-height: 500px"></vaadin-grid>
                </vaadin-row>
                <vaadin-row>
                    <div id="footer" class="vaadin-board-cell"></div>
                </vaadin-row>
            </vaadin-board>
        `;
    }
}