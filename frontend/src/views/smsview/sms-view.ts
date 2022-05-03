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
                <vaadin-row>
                    <div id="firstline" class="vaadin-board-cell"></div>
                    <div id="secondline" class="vaadin-board-cell"></div>
                </vaadin-row>
                <vaadin-row>
                    <vaadin-grid id="smsGrid" style="min-height: 500px"></vaadin-grid>
                </vaadin-row>
                <vaadin-row>
                    <div id="footer"></div>
                </vaadin-row>
            </vaadin-board>
        `;
    }
}